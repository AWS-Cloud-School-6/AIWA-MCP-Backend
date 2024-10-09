package AIWA.McpBackend.service.terraform;

import AIWA.McpBackend.service.s3.S3Service;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import net.schmizz.sshj.common.IOUtils;
import org.springframework.stereotype.Service;

import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;


@Service
@RequiredArgsConstructor
public class TerraformService {

    private final S3Service s3Service;
    private final AmazonS3 s3Client;

    // 원격 Terraform 서버 정보
    @Value("${terraform.remote.host}")
    private String remoteHost;

    @Value("${terraform.remote.port}")
    private int remotePort;

    @Value("${terraform.remote.username}")
    private String remoteUsername;

    @Value("${terraform.remote.privateKeyPath}")
    private String privateKeyPath;

    public void executeTerraform(String userId) throws Exception {

        boolean check_Flag=false;
        String userPrefix = "users/" + userId + "/";

        // 1. S3에서 파일 다운로드
        File mainTfFile = downloadFileFromS3(userPrefix + "main.tf");
        File tfVarsFile = downloadFileFromS3(userPrefix + "terraform.tfvars");

        // 2. terraform.tfstate 파일 존재 여부 확인 후 다운로드
        String tfStateS3Key = userPrefix + "terraform.tfstate";
        File tfStateFile = null;
        if (s3Client.doesObjectExist(s3Service.getBucketName(), tfStateS3Key)) {
            tfStateFile = downloadFileFromS3(tfStateS3Key);
        } else {
            // tfstate 파일이 S3에 존재하지 않는 경우, 빈 임시 파일 생성
            check_Flag=true;
            tfStateFile = File.createTempFile("terraform.tfstate", null);
            Files.write(tfStateFile.toPath(), "{}".getBytes(StandardCharsets.UTF_8));
        }

        SSHClient ssh = new SSHClient();
        ssh.addHostKeyVerifier(new PromiscuousVerifier()); // 보안상 위험, 실제 운영 환경에서는 호스트 키 검증을 설정하세요.

        try {
            // 3. SSH 연결 및 인증
            ssh.connect(remoteHost, remotePort);
            ssh.authPublickey(remoteUsername, privateKeyPath);

            // 4. 원격 서버에 파일 전송
            String remoteDir = "/home/" + remoteUsername + "/terraform/" + userId;
            executeRemoteCommand(ssh, "mkdir -p " + remoteDir);

            ssh.newSCPFileTransfer().upload(mainTfFile.getAbsolutePath(), remoteDir + "/main.tf");
            ssh.newSCPFileTransfer().upload(tfVarsFile.getAbsolutePath(), remoteDir + "/terraform.tfvars");

            // terraform.tfstate 파일이 존재하는 경우에만 업로드
            if (!check_Flag) {
                ssh.newSCPFileTransfer().upload(tfStateFile.getAbsolutePath(), remoteDir + "/terraform.tfstate");
            }

            // 5. 원격 서버에서 Terraform 명령 실행
            executeRemoteCommand(ssh, "cd " + remoteDir + " && terraform init");
            executeRemoteCommand(ssh, "cd " + remoteDir + " && terraform apply -auto-approve");

            // 6. 업데이트된 상태 파일을 원격 서버에서 가져오기 및 S3에 업로드
            ssh.newSCPFileTransfer().download(remoteDir + "/terraform.tfstate", tfStateFile.getAbsolutePath());

            // 업데이트된 상태 파일을 S3에 업로드
            uploadFileToS3(tfStateFile, tfStateS3Key);

            // 7. 원격 서버의 디렉토리 삭제
            executeRemoteCommand(ssh, "rm -rf " + remoteDir);

        } catch (IOException e) {
            throw new Exception("Terraform 실행 중 오류 발생: " + e.getMessage(), e);
        } finally {
            // 8. SSH 연결 종료 및 로컬 임시 파일 삭제
            try {
                if (ssh.isConnected()) {
                    ssh.disconnect();
                }
            } catch (IOException e) {
                // 연결 종료 중 예외 발생 시 무시
            }

            deleteTempFile(mainTfFile);
            deleteTempFile(tfVarsFile);
            if (tfStateFile != null) {
                deleteTempFile(tfStateFile);
            }
        }
    }



    private File downloadFileFromS3(String s3Key) throws IOException {
        S3Object s3Object = s3Client.getObject(s3Service.getBucketName(), s3Key);
        if (s3Object == null || s3Object.getObjectContent() == null) {
            throw new IOException("S3에서 파일을 찾을 수 없습니다: " + s3Key);
        }

        File tempFile = File.createTempFile("temp", null);
        try (InputStream inputStream = s3Object.getObjectContent()) {
            Files.copy(inputStream, tempFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IOException("S3에서 파일을 다운로드하는 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
        return tempFile;
    }

    private void uploadFileToS3(File file, String s3Key) {
        s3Client.putObject(s3Service.getBucketName(), s3Key, file);
    }

    private void executeRemoteCommand(SSHClient ssh, String command) throws IOException {
        try (Session session = ssh.startSession()) {
            Session.Command cmd = session.exec(command);
            cmd.join();

            String output = IOUtils.readFully(cmd.getInputStream()).toString();
            String errorOutput = IOUtils.readFully(cmd.getErrorStream()).toString();

            if (cmd.getExitStatus() != 0) {
                throw new IOException("원격 명령 실행 실패. 명령: " + command + ", 종료 코드: " + cmd.getExitStatus() + ", 에러: " + errorOutput);
            }
        }
    }

    private void deleteTempFile(File file) {
        if (file != null && file.exists()) {
            file.delete();
        }
    }
}
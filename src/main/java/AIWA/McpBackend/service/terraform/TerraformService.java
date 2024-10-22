package AIWA.McpBackend.service.terraform;

import AIWA.McpBackend.service.aws.s3.S3Service;
import lombok.RequiredArgsConstructor;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.List;
@Service
@RequiredArgsConstructor
public class TerraformService {
    private final S3Service s3Service;

    // 원격 Terraform 서버 정보
    @Value("${terraform.remote.host}")
    private String remoteHost;

    @Value("${terraform.remote.port}")
    private int remotePort;

    @Value("${terraform.remote.username}")
    private String remoteUsername;

    // PEM 키 파일이 S3에 저장된 경로
    @Value("${terraform.remote.privateKeyPath}")
    private String pemKeyPath; // S3 경로 (예: "ssh/aiwa-tf-test.pem")


    /**
     * 사용자 ID에 해당하는 Terraform 작업을 실행합니다.
     *
     * @param userId 사용자 ID
     * @throws Exception Terraform 실행 중 발생한 모든 예외
     */
    public void executeTerraform(String userId) throws Exception {
        String userPrefix = "users/" + userId + "/";
        List<String> fileKeys = s3Service.listAllFiles(userId);
        if (fileKeys.isEmpty()) {
            throw new Exception("S3에 Terraform 관련 파일이 없습니다: " + userPrefix);
        }

        String pemKeyPath = "ssh/aiwa-tf-test.pem";
        String pemContent = s3Service.getFileContent(pemKeyPath);
        File tempDir = Files.createTempDirectory("terraform_" + userId).toFile();
        tempDir.deleteOnExit();
        File pemFile = new File(tempDir, "aiwa-tf-test.pem");
        Files.writeString(pemFile.toPath(), pemContent);
        pemFile.setReadable(true);

        SSHClient ssh = new SSHClient();
        ssh.addHostKeyVerifier(new PromiscuousVerifier());

        try {
            ssh.connect(remoteHost, remotePort);
            ssh.authPublickey(remoteUsername, pemFile.getAbsolutePath());

            // Terraform 실행할 디렉토리
            String remoteDir = "/home/" + remoteUsername + "/terraform/" + userId;
            executeRemoteCommand(ssh, "mkdir -p " + remoteDir);

            // S3에서 Terraform 파일 다운로드
            for (String key : fileKeys) {
                String fileName = key.substring(userPrefix.length());
                String downloadCommand = "aws s3 cp s3://" + s3Service.getBucketName() + "/" + key + " " + remoteDir + "/" + fileName;
                executeRemoteCommand(ssh, downloadCommand);
            }

            // Terraform 실행 명령
            String initCommand = "cd " + remoteDir + " && terraform init";
            String applyCommand = "cd " + remoteDir + " && terraform apply -auto-approve";

            // 에러 처리
            try {
                executeRemoteCommand(ssh, initCommand);
                executeRemoteCommand(ssh, applyCommand);
            } catch (IOException e) {
                // 상태 파일 잠금 해제 관련 로직 추가
                // 예: 명령어로 잠금 해제 시도
                throw new Exception("Terraform 실행 중 오류 발생: " + e.getMessage(), e);
            }

            // Terraform 상태 파일 S3로 업로드
            String tfStateRemotePath = remoteDir + "/terraform.tfstate";
            String tfStateS3Key = userPrefix + "terraform.tfstate";
            String downloadStateCommand = "aws s3 cp " + tfStateRemotePath + " s3://" + s3Service.getBucketName() + "/" + tfStateS3Key;
            executeRemoteCommand(ssh, downloadStateCommand);
            executeRemoteCommand(ssh, "rm -rf " + remoteDir);

        } catch (IOException e) {
            throw new Exception("Terraform 실행 중 오류 발생: " + e.getMessage(), e);
        } finally {
            try {
                if (ssh.isConnected()) {
                    ssh.disconnect();
                }
            } catch (IOException e) {
                // 연결 종료 중 예외 발생 시 무시
            }
            deleteDirectoryRecursively(tempDir);
        }
    }

    /**
     * 로컬 디렉토리를 원격 디렉토리에 업로드합니다.
     *
     * @param ssh       SSHClient 객체
     * @param localDir  로컬 디렉토리
     * @param remoteDir 원격 디렉토리
     * @throws IOException 파일 업로드 중 오류가 발생한 경우
     */
    private void uploadDirectory(SSHClient ssh, File localDir, String remoteDir) throws IOException {
        // 재귀적으로 모든 파일 업로드
        for (File file : localDir.listFiles()) {
            if (file.isDirectory()) {
                uploadDirectory(ssh, file, remoteDir + "/" + file.getName());
            } else {
                String relativePath = localDir.toPath().relativize(file.toPath()).toString().replace("\\", "/"); // Windows 경로 호환성
                String remoteFilePath = remoteDir + "/" + relativePath;
                ssh.newSCPFileTransfer().upload(file.getAbsolutePath(), remoteFilePath);
            }
        }
    }

    /**
     * 원격 서버에서 명령어를 실행합니다.
     *
     * @param ssh     SSHClient 객체
     * @param command 실행할 명령어
     * @throws IOException 명령 실행 중 오류가 발생한 경우
     */
    private void executeRemoteCommand(SSHClient ssh, String command) throws IOException {
        try (Session session = ssh.startSession()) {
            Session.Command cmd = session.exec(command);
            cmd.join();

            String errorOutput = IOUtils.readFully(cmd.getErrorStream()).toString();

            if (cmd.getExitStatus() != 0) {
                throw new IOException("원격 명령 실행 실패. 명령: " + command + ", 종료 코드: " + cmd.getExitStatus() + ", 에러: " + errorOutput);
            }
        }
    }



    /**
     * 디렉토리를 재귀적으로 삭제합니다.
     *
     * @param directory 삭제할 디렉토리
     */
    private void deleteDirectoryRecursively(File directory) {
        if (directory.isDirectory()) {
            for (File file : directory.listFiles()) {
                deleteDirectoryRecursively(file);
            }
        }
        directory.delete();
    }
}
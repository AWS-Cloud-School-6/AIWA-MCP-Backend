package AIWA.McpBackend.service.terraform;

import AIWA.McpBackend.service.aws.s3.S3Service;
import com.amazonaws.services.s3.AmazonS3;
import lombok.RequiredArgsConstructor;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.common.IOUtils;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
import net.schmizz.sshj.userauth.keyprovider.KeyProvider;
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

        // 1. S3에서 모든 파일 목록 가져오기
        List<String> fileKeys = s3Service.listAllFiles(userId);
        if (fileKeys.isEmpty()) {
            throw new Exception("S3에 Terraform 관련 파일이 없습니다: " + userPrefix);
        }

        // 2. PEM 파일을 S3에서 다운로드
        String pemKeyPath = "ssh/aiwa-tf-test.pem"; // S3 내 PEM 파일 경로
        String pemContent = s3Service.getFileContent(pemKeyPath); // PEM 파일 다운로드

        // 3. 임시 디렉토리 생성
        File tempDir = Files.createTempDirectory("terraform_" + userId).toFile();
        tempDir.deleteOnExit();

        // 4. PEM 파일을 임시 디렉토리에 저장
        File pemFile = new File(tempDir, "aiwa-tf-test.pem");
        Files.write(pemFile.toPath(), pemContent.getBytes(StandardCharsets.UTF_8));
        pemFile.setReadable(true); // 읽기 권한 설정

        // 5. 모든 파일 다운로드
        for (String key : fileKeys) {
            String relativePath = key.substring(userPrefix.length());
            File localFile = new File(tempDir, relativePath);
            localFile.getParentFile().mkdirs();
            String content = s3Service.getFileContent(key);
            Files.write(localFile.toPath(), content.getBytes(StandardCharsets.UTF_8));
        }

        SSHClient ssh = new SSHClient();
        ssh.addHostKeyVerifier(new PromiscuousVerifier()); // 보안상 위험, 실제 운영 환경에서는 호스트 키 검증을 설정하세요.

        try {
            // 6. SSH 연결 및 인증 (PEM 파일 사용)
            ssh.connect(remoteHost, remotePort);
            // KeyProvider 사용하여 PEM 키 인증
            ssh.authPublickey(remoteUsername, pemFile.getAbsolutePath()); // PEM 파일 사용

            // 7. 원격 디렉토리 설정
            String remoteDir = "/home/" + remoteUsername + "/terraform/" + userId;
            executeRemoteCommand(ssh, "mkdir -p " + remoteDir);

            // 8. 로컬 디렉토리의 모든 파일을 원격 디렉토리에 업로드
            uploadDirectory(ssh, tempDir, remoteDir);

            // 9. Terraform 명령 실행
            executeRemoteCommand(ssh, "cd " + remoteDir + " && terraform init");
            executeRemoteCommand(ssh, "cd " + remoteDir + " && terraform apply -auto-approve");

            // 10. 상태 파일 다운로드
            String tfStateRemotePath = remoteDir + "/terraform.tfstate";
            File tfStateFile = File.createTempFile("terraform.tfstate", null);
            ssh.newSCPFileTransfer().download(tfStateRemotePath, tfStateFile.getAbsolutePath());

            // 11. 상태 파일을 S3에 업로드
            String tfStateS3Key = userPrefix + "terraform.tfstate";
            String tfStateContent = new String(Files.readAllBytes(tfStateFile.toPath()), StandardCharsets.UTF_8);
            s3Service.uploadFileContent(tfStateS3Key, tfStateContent);

            // 12. 원격 디렉토리 삭제
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
            // 임시 디렉토리 삭제
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
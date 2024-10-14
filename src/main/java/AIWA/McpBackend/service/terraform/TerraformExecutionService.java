package AIWA.McpBackend.service.terraform;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TerraformExecutionService {

    /**
     * 전달된 파일로 Terraform 작업을 수행합니다.
     * @param files 파일명과 내용으로 이루어진 Map
     * @return Terraform 상태 파일 내용
     * @throws Exception Terraform 실행 중 발생한 예외
     */
    public String executeTerraform(Map<String, String> files) throws Exception {
        // 1. 임시 디렉터리 생성
        File tempDir = Files.createTempDirectory("terraform").toFile();
        tempDir.deleteOnExit();

        // 2. 파일들을 임시 디렉터리에 저장
        for (Map.Entry<String, String> entry : files.entrySet()) {
            File file = new File(tempDir, entry.getKey());
            Files.write(file.toPath(), entry.getValue().getBytes(StandardCharsets.UTF_8));
        }

        // 3. Terraform 명령 실행
        ProcessBuilder builder = new ProcessBuilder(
                "sh", "-c", "cd " + tempDir.getAbsolutePath() + " && terraform init && terraform apply -auto-approve"
        );
        builder.inheritIO();
        Process process = builder.start();
        if (process.waitFor() != 0) {
            throw new IOException("Terraform 명령 실행 실패");
        }

        // 4. 상태 파일 읽기
        File tfStateFile = new File(tempDir, "terraform.tfstate");
        return new String(Files.readAllBytes(tfStateFile.toPath()), StandardCharsets.UTF_8);
    }
}
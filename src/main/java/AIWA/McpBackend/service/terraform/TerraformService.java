package AIWA.McpBackend.service.terraform;


import AIWA.McpBackend.service.aws.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class TerraformService {

    private final S3Service s3Service;
    private final RestTemplate restTemplate;

    @Value("${terraform.remote.url}")
    private String terraformUrl;  // 두 번째 EKS API 서버 주소

    /**
     * Terraform 작업을 실행합니다.
     *
     * @param userId 사용자 ID
     * @throws Exception Terraform 실행 중 발생한 예외
     */
    public void executeTerraform(String userId) throws Exception {
        // 1. 사용자 파일 목록을 S3에서 가져오기
        List<String> fileKeys = s3Service.listAllFiles("users/" + userId + "/");
        if (fileKeys.isEmpty()) {
            throw new Exception("S3에 Terraform 관련 파일이 없습니다: users/" + userId);
        }

        // 2. 파일 내용을 Map에 저장
        Map<String, String> files = new HashMap<>();
        for (String key : fileKeys) {
            String content = s3Service.getFileContent(key);
            String fileName = key.substring(key.lastIndexOf('/') + 1); // 파일명 추출
            files.put(fileName, content);
        }

        // 3. 두 번째 EKS에 Terraform 작업 요청
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> request = new HttpEntity<>(files, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                terraformUrl + "/terraform/execute", request, String.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            throw new Exception("Terraform 작업 실패: " + response.getBody());
        }

        // 4. 상태 파일 S3에 업로드
        String tfStateContent = response.getBody();
        s3Service.uploadFileContent("users/" + userId + "/terraform.tfstate", tfStateContent);
    }
}
package AIWA.McpBackend.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 s3Client;
    private final String bucketName = "aiwa-terraform";

    public void createUserDirectory(String userId) {
        String userPrefix = "users/" + userId + "/";
        // 초기 main.tf 및 terraform.tfstate 파일을 업로드합니다
        uploadInitialFiles(userPrefix);
    }

    private void uploadInitialFiles(String userPrefix) {
        // 초기 main.tf 파일
        String mainTfContent = """
            variable "aws_access_key" {
              description = "AWS Access Key"
              type        = string
            }

            variable "aws_secret_key" {
              description = "AWS Secret Key"
              type        = string
            }

            provider "aws" {
              region     = "ap-northeast-2"
              access_key = var.aws_access_key  // 변수로 AWS Access Key 제공
              secret_key = var.aws_secret_key  // 변수로 AWS Secret Key 제공
            }
            """;

        s3Client.putObject(bucketName, userPrefix + "main.tf", mainTfContent);

//        // 빈 상태 파일
//        String emptyState = "{}";
//        s3Client.putObject(bucketName, userPrefix + "terraform.tfstate", emptyState);
    }

    public void createTfvarsFile(String userId, String accessKey, String secretKey) {
        String userPrefix = "users/" + userId + "/";
        String tfvarsContent = String.format("""
            aws_access_key = "%s"
            aws_secret_key = "%s"
            """, accessKey, secretKey);

        s3Client.putObject(bucketName, userPrefix + "terraform.tfvars", tfvarsContent);
    }


    /**
     * S3에서 특정 사용자 디렉토리 내 모든 파일 목록을 가져옵니다.
     *
     * @param userId 사용자 ID
     * @return 파일 키 목록
     */
    public List<String> listAllFiles(String userId) {
        String prefix = "users/" + userId + "/";
        ListObjectsV2Request request = new ListObjectsV2Request()
                .withBucketName(bucketName)
                .withPrefix(prefix)
                .withDelimiter("/"); // 디렉토리 구분을 위한 구분자 설정
        ListObjectsV2Result result;
        List<String> fileKeys = new ArrayList<>();

        do {
            result = s3Client.listObjectsV2(request);
            for (S3ObjectSummary objectSummary : result.getObjectSummaries()) {
                String key = objectSummary.getKey();
                if (!key.endsWith("/")) { // 폴더가 아닌 파일만 추가
                    fileKeys.add(key);
                }
            }
            request.setContinuationToken(result.getNextContinuationToken());
        } while (result.isTruncated());

        return fileKeys;
    }

    /**
     * S3에서 지정된 키에 해당하는 파일을 다운로드하여 문자열로 반환합니다.
     *
     * @param s3Key S3 객체 키
     * @return 파일 내용
     * @throws IOException 파일을 가져오는 중 오류가 발생한 경우
     */
    public String getFileContent(String s3Key) throws IOException {
        S3Object s3Object = s3Client.getObject(bucketName, s3Key);
        if (s3Object == null || s3Object.getObjectContent() == null) {
            throw new IOException("S3에서 파일을 찾을 수 없습니다: " + s3Key);
        }

        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(s3Object.getObjectContent(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
        }

        return contentBuilder.toString();
    }

    /**
     * S3에 파일 내용을 업로드합니다.
     *
     * @param s3Key  S3 객체 키
     * @param content 업로드할 내용
     */
    public void uploadFileContent(String s3Key, String content) {
        s3Client.putObject(bucketName, s3Key, content);
    }

    /**
     * S3에서 지정된 키에 해당하는 파일을 삭제합니다.
     *
     * @param s3Key S3 객체 키
     */
    public void deleteFile(String s3Key) {
        s3Client.deleteObject(bucketName, s3Key);
    }

    public String getBucketName() {
        return bucketName;
    }

}
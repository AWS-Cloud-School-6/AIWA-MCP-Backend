package AIWA.McpBackend.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;

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

        // 빈 상태 파일
        String emptyState = "{}";
        s3Client.putObject(bucketName, userPrefix + "terraform.tfstate", emptyState);
    }

    public void createTfvarsFile(String userId, String accessKey, String secretKey) {
        String userPrefix = "users/" + userId + "/";
        String tfvarsContent = String.format("""
            aws_access_key = "%s"
            aws_secret_key = "%s"
            """, accessKey, secretKey);

        s3Client.putObject(bucketName, userPrefix + "terraform.tfvars", tfvarsContent);
    }

    public void downloadFile(String s3Key, String localFilePath) {
        s3Client.getObject(new GetObjectRequest(bucketName, s3Key), new File(localFilePath));
    }

    public void uploadFile(String localFilePath, String s3Key) {
        s3Client.putObject(new PutObjectRequest(bucketName, s3Key, new File(localFilePath)));
    }
}
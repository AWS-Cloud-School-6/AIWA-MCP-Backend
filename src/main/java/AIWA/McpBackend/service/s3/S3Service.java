package AIWA.McpBackend.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStreamReader;
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



    // main.tf 파일 내용 가져오기
    public String getMainTfContent(String userId) throws Exception {
        String key = "users/" + userId + "/main.tf";
        return getFileContentFromS3(key);
    }

    // terraform.tfvars 파일 내용 가져오기
    public String getTfVarsContent(String userId) throws Exception {
        String key = "users/" + userId + "/terraform.tfvars";
        return getFileContentFromS3(key);
    }

    // terraform.tfstate 파일 내용 가져오기
    public String getTfStateContent(String userId) throws Exception {
        String key = "users/" + userId + "/terraform.tfstate";
        return getFileContentFromS3(key);
    }

    // main.tf 파일 업로드
    public void uploadMainTfContent(String userId, String content) {
        String key = "users/" + userId + "/main.tf";
        s3Client.putObject(bucketName, key, content);
    }

    // S3에서 파일 내용을 문자열로 가져오는 메서드
    public String getFileContentFromS3(String key) throws Exception {
        S3Object s3Object = s3Client.getObject(bucketName, key);

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(s3Object.getObjectContent(), StandardCharsets.UTF_8))) {

            StringBuilder content = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                content.append(line).append('\n');
            }

            return content.toString();

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(key + " 파일을 읽는 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    public String getBucketName() {
        return bucketName;
    }

}
package AIWA.McpBackend.service.s3;

import com.amazonaws.services.s3.AmazonS3;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class S3Service {

    private final AmazonS3 s3Client;
    private final String bucketName = "aiwa-terraform";

    public void createUserDirectory(String userId) {
        String userPrefix = "users/" + userId + "/";

        // S3에 빈 파일을 업로드해서 디렉토리 구조 생성
        s3Client.putObject(bucketName, userPrefix, "");

        // Terraform 파일 초기화
        uploadInitialTerraformFiles(userPrefix);
    }

    private void uploadInitialTerraformFiles(String userPrefix) {
        // main.tf 파일 초기화
        String mainTfContent = """
            provider "aws" {
              region = "us-east-1"
            }
            resource "aws_instance" "example" {
              ami           = "ami-0c55b159cbfafe1f0"
              instance_type = "t2.micro"
              tags = {
                Name = "Terraform-EC2"
              }
            }
        """;

        s3Client.putObject(bucketName, userPrefix + "main.tf", mainTfContent);

        // 빈 상태 파일 초기화
        String emptyState = "{}";
        s3Client.putObject(bucketName, userPrefix + "terraform.tfstate", emptyState);
    }

}
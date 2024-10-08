package AIWA.McpBackend.service.ec2;

import AIWA.McpBackend.api.dto.ec2.Ec2RequestDto;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.springframework.stereotype.Service;

import java.io.*;

@Service
public class Ec2Service {

    // 사용자 요청을 바탕으로 Terraform 명령 실행
// 사용자 요청을 바탕으로 Terraform 명령 실행
    public String createEC2Instance(Ec2RequestDto ec2Request, String username) throws JSchException, IOException {
        // SSH를 통해 Terraform이 설치된 EC2에 접속
        JSch jsch = new JSch();
        jsch.addIdentity("/path/to/private-key.pem");

        // SSH 세션 설정 (Terraform EC2 인스턴스에 연결)
        Session session = jsch.getSession("ec2-user", "terraform-instance-ip", 22);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();

        // 사용자별 워크스페이스 선택
        String command = "cd /path/to/terraform/" + username + " && terraform workspace select " + username;
        String result = executeRemoteCommand(session, command);

        // 워크스페이스가 없으면 새로 생성
        if (!result.contains("Workspace selected")) {
            command = "cd /path/to/terraform/" + username + " && terraform workspace new " + username;
            result = executeRemoteCommand(session, command);
        }

        // Terraform 코드 생성 및 저장
        String terraformCode = generateTerraformCode(ec2Request, username);
        saveTerraformFile(terraformCode, username);

        // Terraform 명령어 실행 (init 및 apply)
        command = String.format("cd /path/to/terraform/%s && terraform init && terraform apply -auto-approve", username);
        result = executeRemoteCommand(session, command);

        session.disconnect();
        return result;
    }

    // SSH를 통해 원격 명령어 실행
    private String executeRemoteCommand(Session session, String command) throws JSchException, IOException {
        ChannelExec channelExec = (ChannelExec) session.openChannel("exec");
        channelExec.setCommand(command);
        InputStream inputStream = channelExec.getInputStream();
        channelExec.connect();

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder output = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            output.append(line).append("\n");
        }

        channelExec.disconnect();
        return output.toString();
    }

    // Terraform 코드 생성
    private String generateTerraformCode(Ec2RequestDto ec2Request, String username) {
        return """
            terraform {
              backend "s3" {
                bucket         = "your-tfstate-bucket"
                key            = "terraform/%s/terraform.tfstate"  # 사용자 또는 워크스페이스 이름 사용
                region         = "us-west-2"
                dynamodb_table = "terraform-lock"
              }
            }

            provider "aws" {
              region = "us-west-2"
            }

            resource "aws_instance" "example" {
              ami           = "%s"
              instance_type = "%s"
              key_name      = "%s"
              subnet_id     = "%s"
              vpc_security_group_ids = ["%s"]

              tags = {
                Name = "%s"  # 인스턴스 이름은 태그로 지정
              }
            }
            """.formatted(username,  // 사용자 또는 워크스페이스 이름을 사용하여 상태 파일을 구분
                ec2Request.getAmiId(),
                ec2Request.getInstanceType(),
                ec2Request.getKeyName(),
                ec2Request.getSubnetId(),
                ec2Request.getSecurityGroupIds(),
                ec2Request.getInstanceName());
    }

    // Terraform 코드를 파일로 저장
// Terraform 코드를 파일로 저장 (사용자별로 디렉토리 생성)
    private void saveTerraformFile(String terraformCode, String username) throws IOException {
        String userDirectory = "/local/path/to/terraform/" + username;
        String filePath = userDirectory + "/main.tf";

        // 사용자별 디렉토리 존재 여부 확인 및 생성
        File dir = new File(userDirectory);
        if (!dir.exists()) {
            dir.mkdirs();  // 사용자별 디렉토리 생성
        }

        // Terraform 코드를 해당 사용자 디렉토리에 저장
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(terraformCode);
        }
    }
}
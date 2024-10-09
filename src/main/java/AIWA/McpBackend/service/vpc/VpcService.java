package AIWA.McpBackend.service.vpc;

import AIWA.McpBackend.provider.aws.api.dto.vpc.VpcRequestDto;
import AIWA.McpBackend.service.s3.S3Service;
import AIWA.McpBackend.service.terraform.TerraformService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VpcService {

    private final S3Service s3Service;

    private final TerraformService terraformService;
    public void createVpc(VpcRequestDto vpcRequest, String userId) throws Exception {
        // 1. 기존 main.tf 내용 가져오기
        String mainTfContent = s3Service.getMainTfContent(userId);

        // 2. VPC 코드 블록 생성
        String vpcResource = String.format("""
                resource "aws_vpc" "%s" {
                  cidr_block = "%s"
                  tags = {
                    Name = "%s"
                  }
                }
                """, vpcRequest.getVpcName(), vpcRequest.getCidrBlock(), vpcRequest.getVpcName());

        // 3. 기존 main.tf에 VPC 코드 블록 추가
        mainTfContent += "\n" + vpcResource;

        System.out.println(mainTfContent);
        // 4. 수정된 main.tf를 S3에 업로드
        s3Service.uploadMainTfContent(userId, mainTfContent);

        // 5. Terraform 실행 요청
        terraformService.executeTerraform(userId);
    }


}

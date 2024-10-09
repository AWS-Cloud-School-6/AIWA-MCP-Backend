package AIWA.McpBackend.service.securitygroup;

import AIWA.McpBackend.provider.aws.api.dto.securitygroup.SecurityGroupRequestDto;
import AIWA.McpBackend.provider.aws.api.dto.securitygroup.SecurityGroupRuleDto;
import AIWA.McpBackend.service.s3.S3Service;
import AIWA.McpBackend.service.terraform.TerraformService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SecurityGroupService {

    private final S3Service s3Service;
    private final TerraformService terraformService;

    /**
     * Security Group을 생성합니다.
     *
     * @param securityGroupRequest Security Group 생성 요청 DTO
     * @param userId               사용자 ID
     * @throws Exception Security Group 생성 중 발생한 예외
     */
    public void createSecurityGroup(SecurityGroupRequestDto securityGroupRequest, String userId) throws Exception {
        // 1. Security Group 리소스 블록 생성
        StringBuilder sgTfContent = new StringBuilder(String.format("""
                resource "aws_security_group" "%s" {
                  vpc_id = aws_vpc.%s.id
                  tags = {
                    Name = "%s"
                  }
                }
                """,
                securityGroupRequest.getSecurityGroupName(),
                securityGroupRequest.getVpcName(),
                securityGroupRequest.getSecurityGroupName()));

        // 2. 인바운드 규칙 추가
        appendSecurityGroupRules(sgTfContent, securityGroupRequest.getInboundRules(), "ingress");

        // 3. 아웃바운드 규칙 추가
        appendSecurityGroupRules(sgTfContent, securityGroupRequest.getOutboundRules(), "egress");

        // 4. Security Group .tf 파일 이름 설정
        String sgTfFileName = String.format("security_group_%s.tf", securityGroupRequest.getSecurityGroupName());

        // 5. S3에 Security Group .tf 파일 업로드
        String s3Key = "users/" + userId + "/" + sgTfFileName;
        s3Service.uploadFileContent(s3Key, sgTfContent.toString());

        // 6. Terraform 실행 요청
        terraformService.executeTerraform(userId);
    }

    /**
     * Security Group 규칙을 .tf 파일에 추가합니다.
     *
     * @param contentBuilder StringBuilder에 규칙을 추가
     * @param rules          Security Group 규칙 리스트
     * @param ruleType       규칙 타입 (ingress 또는 egress)
     */
    private void appendSecurityGroupRules(StringBuilder contentBuilder, List<SecurityGroupRuleDto> rules, String ruleType) {
        for (SecurityGroupRuleDto rule : rules) {
            contentBuilder.append(String.format("""
                    %s {
                      protocol = "%s"
                      from_port = "%s"
                      to_port = "%s"
                      cidr_blocks = ["%s"]
                    }
                    """,
                    ruleType,
                    rule.getProtocol(),
                    rule.getFromPort(),
                    rule.getToPort(),
                    rule.getCidrBlock()));
        }
    }

    /**
     * Security Group을 삭제합니다.
     *
     * @param securityGroupName Security Group 이름
     * @param userId            사용자 ID
     * @throws Exception Security Group 삭제 중 발생한 예외
     */
    public void deleteSecurityGroup(String securityGroupName, String userId) throws Exception {
        // 1. 삭제하려는 Security Group .tf 파일 이름 설정
        String sgTfFileName = String.format("security_group_%s.tf", securityGroupName);

        // 2. S3에서 해당 Security Group .tf 파일 삭제
        String s3Key = "users/" + userId + "/" + sgTfFileName;
        s3Service.deleteFile(s3Key);

        // 3. Terraform 실행 요청
        terraformService.executeTerraform(userId);
    }
}
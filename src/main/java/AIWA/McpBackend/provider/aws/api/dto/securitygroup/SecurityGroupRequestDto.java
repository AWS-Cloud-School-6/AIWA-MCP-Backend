package AIWA.McpBackend.provider.aws.api.dto.securitygroup;

import lombok.Data;

import java.util.List;

@Data
public class SecurityGroupRequestDto {
    private String securityGroupName;  // Security Group 이름
    private String vpcName;            // VPC 이름
    private List<SecurityGroupRuleDto> inboundRules;  // 인바운드 규칙 리스트
    private List<SecurityGroupRuleDto> outboundRules; // 아웃바운드 규칙 리스트
}
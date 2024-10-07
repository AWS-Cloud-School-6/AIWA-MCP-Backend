package AIWA.McpBackend.api.dto.ec2;

import lombok.Data;

@Data
public class Ec2RequestDto {

    // 필수 파라미터들
    private String amiId;              // AMI ID (예: ami-0c55b159cbfafe1f0)
    private String instanceType;       // 인스턴스 타입 (예: t2.micro)
    private String keyName;            // EC2 인스턴스에 접근할 키 페어 이름
    private String securityGroupIds;   // 보안 그룹 ID (예: sg-0123456789abcdef0)
    private String subnetId;           // 서브넷 ID (예: subnet-0123456789abcdef0)
    private String instanceName;       // 인스턴스에 부여할 이름 (태그)

}

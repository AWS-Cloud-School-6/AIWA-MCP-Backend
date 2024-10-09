package AIWA.McpBackend.provider.aws.api.dto.internetGateway;

import lombok.Data;

@Data
public class InternetGatewayRequestDto {
    private String igwName;  // Internet Gateway의 이름
    private String vpcName;  // VPC와 연결할 때 사용할 VPC 이름
}
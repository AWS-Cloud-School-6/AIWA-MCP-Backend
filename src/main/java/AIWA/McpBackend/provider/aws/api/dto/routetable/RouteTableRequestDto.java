package AIWA.McpBackend.provider.aws.api.dto.routetable;

import lombok.Data;

@Data
public class RouteTableRequestDto {
    private String routeTableName;  // Route Table의 이름
    private String vpcName;         // Route Table이 연결될 VPC 이름
}
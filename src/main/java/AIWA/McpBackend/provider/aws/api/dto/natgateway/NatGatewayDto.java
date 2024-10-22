package AIWA.McpBackend.provider.aws.api.dto.natgateway;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.util.Map;

@Getter
public class NatGatewayDto {
    private String natGatewayId; // NAT Gateway ID
    private String state; // 상태 (예: "available", "pending")
    private Map<String, String> tags; // 태그
    private String vpcId; // 연결된 VPC ID

    public NatGatewayDto(String natGatewayId, String state, Map<String, String> tags, String vpcId) {

        this.natGatewayId = natGatewayId;
        this.state = state;
        this.tags = tags;
        this.vpcId = vpcId;
    }
}
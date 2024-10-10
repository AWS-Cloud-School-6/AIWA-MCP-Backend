package AIWA.McpBackend.provider.aws.api.dto.eip;

import lombok.Data;

@Data
public class EipRequestDto {
    private String instanceId;  // EC2 인스턴스 ID
    private String eipId;       // EIP ID (삭제할 때 사용)
    private String userId;      // 사용자 ID
}
package AIWA.McpBackend.provider.aws.api.dto.eip;

import lombok.Data;

@Data
public class EipRequestDto {
    private String eipId;
    private String userId;      // 사용자 ID
}
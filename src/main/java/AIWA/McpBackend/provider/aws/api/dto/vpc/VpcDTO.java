// VpcDTO.java
package AIWA.McpBackend.provider.aws.api.dto.vpc;

import lombok.Getter;

import java.util.Map;

@Getter
public class VpcDTO {
    private final String vpcId;
    private final String cidr;
    private final Map<String, String> tags;

    public VpcDTO(String vpcId, String cidr, Map<String, String> tags) {
        this.vpcId = vpcId;
        this.cidr = cidr;
        this.tags = tags;
    }
}

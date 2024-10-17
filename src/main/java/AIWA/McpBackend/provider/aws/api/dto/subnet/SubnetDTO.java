// SubnetDTO.java
package AIWA.McpBackend.provider.aws.api.dto.subnet;

import lombok.Getter;

import java.util.Map;

@Getter
public class SubnetDTO {
    private final String subnetId;
    private final String cidr;
    private final String vpcId;
    private final Map<String, String> tags;

    public SubnetDTO(String subnetId, String cidr, String vpcId, Map<String, String> tags) {
        this.subnetId = subnetId;
        this.cidr = cidr;
        this.vpcId = vpcId;
        this.tags = tags;
    }

}

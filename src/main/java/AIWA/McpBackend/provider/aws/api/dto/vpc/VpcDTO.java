// VpcDTO.java
package AIWA.McpBackend.provider.aws.api.dto.vpc;

import lombok.Getter;

@Getter
public class VpcDTO {
    private String vpcId;
    private String cidr;

    public VpcDTO(String vpcId, String cidr) {
        this.vpcId = vpcId;
        this.cidr = cidr;
    }

    // Getters
    public String getVpcId() {
        return vpcId;
    }

    public String getCidr() {
        return cidr;
    }
}

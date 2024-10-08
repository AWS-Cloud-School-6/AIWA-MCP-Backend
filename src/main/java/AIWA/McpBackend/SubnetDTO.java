// SubnetDTO.java
package AIWA.McpBackend;

import lombok.Getter;

@Getter
public class SubnetDTO {
    private String subnetId;
    private String cidr;

    public SubnetDTO(String subnetId, String cidr) {
        this.subnetId = subnetId;
        this.cidr = cidr;
    }

    // Getters
    public String getSubnetId() {
        return subnetId;
    }

    public String getCidr() {
        return cidr;
    }
}

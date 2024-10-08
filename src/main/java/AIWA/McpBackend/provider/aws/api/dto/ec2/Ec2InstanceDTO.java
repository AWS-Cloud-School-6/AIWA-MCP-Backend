// EC2InstanceDTO.java
package AIWA.McpBackend.provider.aws.api.dto.ec2;

import lombok.Getter;

@Getter
public class Ec2InstanceDTO {
    private String instanceId;
    private String state;

    public Ec2InstanceDTO(String instanceId, String state) {
        this.instanceId = instanceId;
        this.state = state;
    }

    // Getters
    public String getInstanceId() {
        return instanceId;
    }

    public String getState() {
        return state;
    }
}

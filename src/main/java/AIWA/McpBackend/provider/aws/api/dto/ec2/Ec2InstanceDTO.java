// EC2InstanceDTO.java
package AIWA.McpBackend.provider.aws.api.dto.ec2;

import lombok.Data;
import lombok.Getter;

import java.util.Map;

@Getter
public record Ec2InstanceDTO(String instanceId, String state, Map<String, String> tags) {

}

// SecurityGroupDTO.java
package AIWA.McpBackend.controller.api.dto.securitygroup;

import lombok.Getter;

import java.util.Map;

@Getter
public class SecurityGroupDTO {
    private final String groupId;
    private final String groupName;
    private final Map<String, String> tags;
    private final String vpcId;

    public SecurityGroupDTO(String groupId, String groupName, Map<String, String> tags, String vpcId) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.tags = tags;
        this.vpcId = vpcId;
    }

}

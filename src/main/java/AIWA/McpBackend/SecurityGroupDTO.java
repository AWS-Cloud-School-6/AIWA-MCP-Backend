// SecurityGroupDTO.java
package AIWA.McpBackend;

import lombok.Getter;

@Getter
public class SecurityGroupDTO {
    private String groupId;
    private String groupName;

    public SecurityGroupDTO(String groupId, String groupName) {
        this.groupId = groupId;
        this.groupName = groupName;
    }

    // Getters
    public String getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }
}

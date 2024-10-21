package AIWA.McpBackend.provider.aws.api.dto.membercredential;

import lombok.Data;

@Data
public record MemberRequestDto(String name, String password, String email) {

}

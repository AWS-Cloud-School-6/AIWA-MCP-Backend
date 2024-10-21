package AIWA.McpBackend.provider.aws.api.dto.membercredential;

import lombok.Data;
import lombok.Getter;

@Getter
public record MemberRequestDto(String name, String password, String email) {

}

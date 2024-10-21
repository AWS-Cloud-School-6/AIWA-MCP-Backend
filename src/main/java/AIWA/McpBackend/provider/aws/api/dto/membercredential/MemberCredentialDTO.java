package AIWA.McpBackend.provider.aws.api.dto.membercredential;

import lombok.Data;

@Data
public record MemberCredentialDTO(String email, String access_key, String secret_key) {

}

package AIWA.McpBackend.provider.aws.api.dto.membercredential;

import lombok.Getter;

@Getter
public class MemberCredentialDTO {
    private final String email;
    private final String access_key;
    private final String secret_key;

    public MemberCredentialDTO(String email, String access_key, String secret_key) {
        this.email = email;
        this.access_key = access_key;
        this.secret_key = secret_key;
    }

}

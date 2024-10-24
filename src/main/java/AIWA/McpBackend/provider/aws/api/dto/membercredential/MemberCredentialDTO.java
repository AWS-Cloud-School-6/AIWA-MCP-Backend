package AIWA.McpBackend.provider.aws.api.dto.membercredential;

import lombok.Data;


@Data
public class MemberCredentialDTO {
    private String email;
    private String accessKey;
    private String secretKey;

    public MemberCredentialDTO(String email, String accessKey, String secretKey) {
        this.email = email;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }
}
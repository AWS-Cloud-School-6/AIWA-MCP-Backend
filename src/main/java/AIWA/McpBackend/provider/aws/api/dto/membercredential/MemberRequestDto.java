package AIWA.McpBackend.provider.aws.api.dto.membercredential;

import lombok.Data;
import lombok.Getter;

import lombok.Getter;

@Getter
public class MemberRequestDto {
    private final String name;
    private final String password;
    private final String email;

    public MemberRequestDto(String name, String password, String email) {
        this.name = name;
        this.password = password;
        this.email = email;
    }
}
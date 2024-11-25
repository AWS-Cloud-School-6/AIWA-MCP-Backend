package AIWA.McpBackend.provider.aws.api.dto.member;

import AIWA.McpBackend.controller.api.dto.membercredential.AiwaKeyResponseDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;

import java.util.List;


@Data
@Getter
public class MemberCredentialDTO {
    private Long id;        // 회원 ID
    private String name;    // 회원 이름
    private String email;   // 회원 이메일

    @JsonIgnore // 비밀번호는 반환하지 않음
    private String password; // 회원 비밀번호

    private List<AiwaKeyResponseDto> aiwaKeys;

    public MemberCredentialDTO(Long id, String name, String email, String password, List<AiwaKeyResponseDto> aiwaKeys) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.aiwaKeys = aiwaKeys;
    }
}
package AIWA.McpBackend.provider.aws.api.dto.membercredential;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor  // 기본 생성자 추가
public class MemberDeleteRequestDto {
    private String email;  // final 제거

    public MemberDeleteRequestDto(String email) {
        this.email = email;
    }
}

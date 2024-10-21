package AIWA.McpBackend.provider.aws.api.dto.membercredential;

import AIWA.McpBackend.entity.member.Member;
import lombok.Data;
import lombok.Getter;

@Getter
public record MemberResponseDto(String userName, String email, String access_key) {

    public static MemberResponseDto toDto(Member member) {
        return new MemberResponseDto(member.getName(),member.getEmail(), member.getAccess_key());
    }
}

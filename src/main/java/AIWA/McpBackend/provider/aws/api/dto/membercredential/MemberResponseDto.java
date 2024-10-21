package AIWA.McpBackend.provider.aws.api.dto.membercredential;

import AIWA.McpBackend.entity.member.Member;
import lombok.Data;

import lombok.Data;

@Data
public class MemberResponseDto {
    private String userName;
    private String email;
    private String accessKey;

    public MemberResponseDto(String userName, String email, String accessKey) {
        this.userName = userName;
        this.email = email;
        this.accessKey = accessKey;
    }

    public static MemberResponseDto toDto(Member member) {
        return new MemberResponseDto(member.getName(), member.getEmail(), member.getAccess_key());
    }
}
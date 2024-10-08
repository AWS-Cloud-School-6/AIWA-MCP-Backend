package AIWA.McpBackend.service.member;

import AIWA.McpBackend.entity.member.Member;
import AIWA.McpBackend.repository.member.MemberRepository;
import AIWA.McpBackend.service.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final S3Service s3Service;

    public Member registerMember(Member member) {
        if (memberRepository.findByEmail(member.getEmail()) != null) {
            throw new RuntimeException("Email already exists");
        }
        System.out.println(member.getName());
        System.out.println(member.getEmail());
        s3Service.createUserDirectory(member.getEmail());
        return memberRepository.save(member);
    }

    // 특정 회원 조회
    public Optional<Member> getMemberById(Long id) {
        return memberRepository.findById(id);
    }

    public Member getMemberByEmail(String email) {
        return memberRepository.findByEmail(email);
    }

    public Member updateCredentials(String email, String accessKey, String secretKey) {
        Member member = getMemberByEmail(email);
        member.setAccess_key(accessKey);
        member.setSecret_key(secretKey);

        s3Service.createTfvarsFile(email,accessKey,secretKey);
        return memberRepository.save(member);
    }
}
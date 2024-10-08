package AIWA.McpBackend.service.member;

import AIWA.McpBackend.entity.member.Member;
import AIWA.McpBackend.repository.member.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    public Member registerMember(Member member) {
        if (memberRepository.findByEmail(member.getEmail()) != null) {
            throw new RuntimeException("Email already exists");
        }
        return memberRepository.save(member);
    }

    // 특정 회원 조회
    public Optional<Member> getMemberById(Long id) {
        return memberRepository.findById(id);
    }


    public Member addOrUpdateKeys(Long id, Map<String, String> keys) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        // 새로운 키가 제공된 경우, insert 또는 update 처리
        if (keys.containsKey("accessKey") && member.getAccess_key() == null) {
            member.setAccess_key(keys.get("accessKey")); // insert
        } else if (keys.containsKey("accessKey")) {
            member.setAccess_key(keys.get("accessKey")); // update
        }

        if (keys.containsKey("secretKey") && member.getSecret_key() == null) {
            member.setSecret_key(keys.get("secretKey")); // insert
        } else if (keys.containsKey("secretKey")) {
            member.setSecret_key(keys.get("secretKey")); // update
        }

        return memberRepository.save(member);
    }

    // Access Key와 Secret Key 삭제
    public Member removeKeys(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Member not found"));

        member.setAccess_key(null);
        member.setSecret_key(null);

        return memberRepository.save(member);
    }
}
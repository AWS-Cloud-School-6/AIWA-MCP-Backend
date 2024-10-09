package AIWA.McpBackend.service.member;

import AIWA.McpBackend.entity.member.Member;
import AIWA.McpBackend.repository.member.MemberRepository;
import AIWA.McpBackend.service.aws.s3.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    private final S3Service s3Service;

    public Member getLoggedInMember() {
        // SecurityContextHolder에서 Authentication 객체 가져오기
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Authentication 객체가 null인지 확인하고 처리
        if (authentication == null || !authentication.isAuthenticated()) {
            return null;  // 로그인되지 않은 상태
        }

        // 인증된 사용자의 principal 객체 확인
        Object principal = authentication.getPrincipal();

        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            // username을 통해 사용자 정보 조회 (예: email)
            return getMemberByEmail(username);
        } else {
            return null; // 인증 정보 없음
        }
    }

    public Member registerMember(Member member) {
        if (memberRepository.findByEmail(member.getEmail()) != null) {
            throw new RuntimeException("Email already exists");
        }
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

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }


    public Member addOrUpdateKeys(String email,String access_key,String secret_key) {
        Member member = getMemberByEmail(email);
        member.setAccess_key(access_key);
        member.setSecret_key(secret_key);
        s3Service.createTfvarsFile(email,access_key,secret_key);
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

    public Member login(String email, String password) {
        // 이메일로 회원 조회
        Member member = memberRepository.findByEmail(email);

        // 비밀번호 검증
        if (member != null && member.getPassword().equals(password)) {
            return member;  // 로그인 성공
        }

        return null;  // 로그인 실패
    }


}
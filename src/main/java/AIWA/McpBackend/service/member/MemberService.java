package AIWA.McpBackend.service.member;

import AIWA.McpBackend.entity.member.Member;
import AIWA.McpBackend.provider.aws.api.dto.membercredential.MemberRequestDto;
import AIWA.McpBackend.repository.member.MemberRepository;
import AIWA.McpBackend.service.aws.s3.S3Service;
//import AIWA.McpBackend.service.kms.KmsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final S3Service s3Service;

    public Member registerMember(MemberRequestDto memberRequestDto) {
        if (memberRepository.findByEmail(memberRequestDto.getEmail()) != null) {
            throw new RuntimeException("Email already exists");
        }
        s3Service.createUserDirectory(memberRequestDto.getEmail());
        Member regiMember=new Member(memberRequestDto.getName(), memberRequestDto.getPassword(), memberRequestDto.getEmail());
        return memberRepository.save(regiMember);
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
        String encrypt_access_key, encrypt_secret_key;
//        encrypt_access_key = kmsService.encrypt(access_key);
//        encrypt_secret_key = kmsService.encrypt(secret_key);

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
}
package AIWA.McpBackend.provider.aws.api.controller.member;

import AIWA.McpBackend.entity.member.Member;
import AIWA.McpBackend.provider.aws.api.dto.membercredential.MemberCredentialDTO;
//import AIWA.McpBackend.service.kms.KmsService;
import AIWA.McpBackend.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;

    // 회원 등록
    @PostMapping("/register")
    public ResponseEntity<Member> registerMember(@RequestBody Member member) {
        Member savedMember = memberService.registerMember(member);
        return ResponseEntity.ok(savedMember);
    }

    // 특정 회원 조회
    @GetMapping("/")
    public ResponseEntity<Member> getMember(@RequestParam String email) {
        Member member = memberService.getMemberByEmail(email);  // 이 메서드가 Optional을 반환하지 않는다고 가정
        if (member != null) {
            return ResponseEntity.ok(member);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Member>> getAllMembers() {
        List<Member> members = memberService.getAllMembers();
        return ResponseEntity.ok(members); // HTTP 200 OK와 함께 members 리스트 반환
    }

    @PostMapping("/update-credentials")
    public ResponseEntity<String> updateCredentials(@RequestBody MemberCredentialDTO memberCredentialDto) {

        memberService.addOrUpdateKeys(memberCredentialDto.getEmail(), memberCredentialDto.getAccess_key(), memberCredentialDto.getSecret_key());
        return ResponseEntity.ok("update wan.");
    }

}
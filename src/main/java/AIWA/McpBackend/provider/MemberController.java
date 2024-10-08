package AIWA.McpBackend.provider;

import AIWA.McpBackend.entity.member.Member;
import AIWA.McpBackend.service.member.MemberService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;

    @Autowired
    public MemberController(MemberService memberService) {
        this.memberService = memberService;
    }

    // 회원 등록
    @PostMapping("/register")
    public ResponseEntity<Member> registerMember(@RequestBody Member member) {
        Member savedMember = memberService.registerMember(member);
        return ResponseEntity.ok(savedMember);
    }

    // 특정 회원 조회
    @GetMapping("/{id}")
    public ResponseEntity<Member> getMember(@PathVariable Long id) {
        return memberService.getMemberById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Access Key와 Secret Key 추가 또는 수정
    @PutMapping("/{id}/keys")
    public ResponseEntity<Member> addOrUpdateKeys(@PathVariable Long id,
                                                  @RequestBody Map<String, String> keys) {
        Member updatedMember = memberService.addOrUpdateKeys(id, keys);
        return ResponseEntity.ok(updatedMember);
    }

    // Access Key와 Secret Key 삭제
    @DeleteMapping("/{id}/keys/delete")
    public ResponseEntity<Member> removeKeys(@PathVariable Long id) {
        Member updatedMember = memberService.removeKeys(id);
        return ResponseEntity.ok(updatedMember);
    }
}
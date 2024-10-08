package AIWA.McpBackend.provider;

import AIWA.McpBackend.entity.member.Member;
import AIWA.McpBackend.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    @GetMapping("/{id}")
    public ResponseEntity<Member> getMember(@PathVariable Long id) {
        return memberService.getMemberById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }


    @PostMapping("/update-credentials")
    public ResponseEntity<String> updateCredentials(@RequestBody Member member) {
        memberService.updateCredentials(member.getEmail(), member.getAccess_key(),member.getSecret_key());
        return ResponseEntity.ok("자격 증명이 업데이트되었습니다.");
    }



}
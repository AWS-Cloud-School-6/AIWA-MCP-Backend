package AIWA.McpBackend.provider.aws.api.controller.member;

import AIWA.McpBackend.entity.member.Member;
import AIWA.McpBackend.provider.aws.api.dto.membercredential.MemberCredentialDTO;
import AIWA.McpBackend.service.kms.KmsService;
import AIWA.McpBackend.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;
    private final KmsService kmsService;

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
    public ResponseEntity<String> updateCredentials(@RequestBody MemberCredentialDTO memberCredentialDto) {

        memberService.addOrUpdateKeys(memberCredentialDto.getEmail(), memberCredentialDto.getAccess_key(), memberCredentialDto.getSecret_key());
        return ResponseEntity.ok("자격 증명이 업데이트되었습니다.");
    }



}
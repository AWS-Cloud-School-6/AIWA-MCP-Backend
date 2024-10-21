package AIWA.McpBackend.provider.aws.api.controller.member;

import AIWA.McpBackend.entity.member.Member;
import AIWA.McpBackend.provider.aws.api.dto.membercredential.MemberCredentialDTO;
//import AIWA.McpBackend.service.kms.KmsService;
import AIWA.McpBackend.provider.aws.api.dto.membercredential.MemberRequestDto;
import AIWA.McpBackend.provider.aws.api.dto.membercredential.MemberResponseDto;
import AIWA.McpBackend.provider.response.CommonResult;
import AIWA.McpBackend.provider.response.ListResult;
import AIWA.McpBackend.provider.response.SingleResult;
import AIWA.McpBackend.service.member.MemberService;
import AIWA.McpBackend.service.response.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;
    private final ResponseService responseService;

    // 회원 등록
    @PostMapping("/register")
    public SingleResult<MemberResponseDto> registerMember(@RequestBody MemberRequestDto memberRequestDto) {
        Member savedMember = memberService.registerMember(memberRequestDto);
        MemberResponseDto memberResponseDto = MemberResponseDto.toDto(savedMember);
        return responseService.getSingleResult(memberResponseDto);
    }

    // 특정 회원 조회
    @GetMapping("/")
    public SingleResult<MemberResponseDto> getMember(@RequestParam String email) {
        Member findMember = memberService.getMemberByEmail(email);  // 이 메서드가 Optional을 반환하지 않는다고 가정
        MemberResponseDto memberResponseDto = MemberResponseDto.toDto(findMember);
        if (findMember != null) {
            return responseService.getSingleResult(memberResponseDto);
        } else {
            return (SingleResult<MemberResponseDto>) responseService.getFailResult();
        }
    }

    @GetMapping("/all")
    public ListResult<MemberResponseDto> getAllMembers() {
        List<Member> members = memberService.getAllMembers();
        List<MemberResponseDto> memberResponseDtoList = members.stream().map(MemberResponseDto::toDto).collect(Collectors.toList());
        return responseService.getListResult(memberResponseDtoList);
    }

    @PostMapping("/update-credentials")
    public CommonResult updateCredentials(@RequestBody MemberCredentialDTO memberCredentialDto) {
        memberService.addOrUpdateKeys(memberCredentialDto.getEmail(),memberCredentialDto.getAccessKey(), memberCredentialDto.getSecretKey());
        return responseService.getSuccessResult();
    }

}
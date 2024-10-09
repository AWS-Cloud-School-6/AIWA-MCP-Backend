package AIWA.McpBackend.provider.aws.api.controller.member;

import AIWA.McpBackend.entity.member.Member;
import AIWA.McpBackend.provider.aws.api.dto.membercredential.MemberCredentialDTO;
import AIWA.McpBackend.service.member.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;

    // 회원 등록 페이지로 이동
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";  // register.html로 이동
    }

    // 회원 등록 처리
    @PostMapping("/register")
    public String registerMember(@ModelAttribute Member member, RedirectAttributes redirectAttributes) {
        memberService.registerMember(member);
        redirectAttributes.addFlashAttribute("message", "회원가입이 완료되었습니다.");
        return "redirect:/";  // 홈 페이지로 리디렉션
    }

    // 자격 증명 업데이트 페이지로 이동
    @GetMapping("/update-credentials")
    public String showUpdateCredentialsPage() {
        return "update-credentials";  // update-credentials.html로 이동
    }

    // 자격 증명 업데이트 처리
    @PostMapping("/update-credentials")
    public String updateCredentials(@ModelAttribute MemberCredentialDTO memberCredentialDto, RedirectAttributes redirectAttributes) {
        memberService.addOrUpdateKeys(memberCredentialDto.getEmail(), memberCredentialDto.getAccess_key(), memberCredentialDto.getSecret_key());
        redirectAttributes.addFlashAttribute("message", "자격 증명이 업데이트되었습니다.");
        return "redirect:/";
    }

    // 로그인 페이지로 이동
    @GetMapping("/login")
    public String loginPage() {
        return "login";  // login.html로 이동
    }

    // 로그인 처리
    @PostMapping("/login")
    public String login(String email, String password, HttpSession session, Model model) {
        Member member = memberService.login(email, password);

        if (member != null) {
            session.setAttribute("user", member);
            return "redirect:/";  // 홈 페이지로 리디렉션
        } else {
            model.addAttribute("error", "잘못된 이메일 또는 비밀번호입니다.");
            return "login";  // 로그인 실패 시 login.html로 다시 이동
        }
    }

    // 로그아웃 처리
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";  // 로그아웃 후 로그인 페이지로 리디렉션
    }
}
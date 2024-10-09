package AIWA.McpBackend.provider;

import AIWA.McpBackend.entity.member.Member;
import AIWA.McpBackend.service.member.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final MemberService memberService;  // 회원 정보를 가져오기 위해

    @GetMapping("/")
    public String home(HttpSession session, Model model) {
        // 세션에서 로그인된 사용자 정보를 가져옴
        Member loggedInMember = (Member) session.getAttribute("user");

        if (loggedInMember != null) {
            // 로그인된 사용자의 이메일을 모델에 추가
            model.addAttribute("email", loggedInMember.getEmail());
        } else {
            // 로그인되지 않았으면 anonymous로 설정
            model.addAttribute("email", "anonymous");
        }

        return "index";  // index.html로 이동
    }
}
package AIWA.McpBackend.provider.aws.api.controller.vpc;

import AIWA.McpBackend.provider.aws.api.dto.vpc.VpcRequestDto;
import AIWA.McpBackend.service.aws.vpc.VpcService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/api/vpc")
@RequiredArgsConstructor
public class VpcController {

    private final VpcService vpcService;

    /**
     * VPC 생성 엔드포인트
     *
     * @param vpcRequest VPC 생성 요청 DTO
     * @param userId     사용자 ID (요청 파라미터로 전달)
     * @return 생성 성공 메시지 또는 오류 메시지
     */
    @PostMapping("/create")
    public String createVpc(
            @ModelAttribute VpcRequestDto vpcRequest,
            @RequestParam String userId) {
        try {
            vpcService.createVpc(vpcRequest, userId);
            return "redirect:/api/vpc/list?userId=" + userId;
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/api/vpc/list?error";
        }
    }

    /**
     * VPC 삭제 엔드포인트
     *
     * @param vpcName VPC 이름 (요청 파라미터로 전달)
     * @param userId  사용자 ID (요청 파라미터로 전달)
     * @return 삭제 성공 메시지 또는 오류 메시지
     */
    @PostMapping("/delete")
    public String deleteVpc(
            @RequestParam String vpcName,
            @RequestParam String userId) {
        try {
            vpcService.deleteVpc(vpcName, userId);
            return "redirect:/api/vpc/list?userId=" + userId;
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/api/vpc/list?error";
        }
    }

    /**
     * 사용자가 생성한 VPC 목록을 조회합니다.
     *
     * @param userId 사용자 ID
     * @param model  VPC 목록을 모델에 추가하여 뷰로 전달
     * @return VPC 목록 페이지
     */
    @GetMapping("/list")
    public String listVpcs(@RequestParam String userId, Model model) {
        List<String> vpcList = vpcService.getUserVpcList(userId);
        if(!vpcList.isEmpty()) {
            System.out.println(vpcList.get(0));
        }
        model.addAttribute("vpcList", vpcList);
        model.addAttribute("userId", userId);
        return "vpc-list";  // vpc-list.html 뷰로 이동
    }
}
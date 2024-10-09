package AIWA.McpBackend.provider.aws.api.controller.subnet;

import AIWA.McpBackend.provider.aws.api.dto.subnet.SubnetRequestDto;
import AIWA.McpBackend.service.aws.subnet.SubnetService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/api/subnet")
@RequiredArgsConstructor
public class SubnetController {

    private final SubnetService subnetService;

    @PostMapping("/create")
    public String createSubnet(@ModelAttribute SubnetRequestDto subnetRequest, @RequestParam String userId) {
        try {
            subnetService.createSubnet(subnetRequest, userId);
            return "redirect:/api/subnet/list?userId=" + userId;  // 성공 시 목록 페이지로 리디렉션
        } catch (Exception e) {
            e.printStackTrace();
            return "Subnet 생성 중 오류가 발생했습니다: " + e.getMessage();
        }
    }

    @PostMapping("/delete")
    public String deleteSubnet(@RequestParam String subnetName, @RequestParam String userId) {
        try {
            subnetService.deleteSubnet(subnetName, userId);
            return "redirect:/api/subnet/list?userId=" + userId;  // 성공 시 목록 페이지로 리디렉션
        } catch (Exception e) {
            e.printStackTrace();
            return "Subnet 삭제 중 오류가 발생했습니다: " + e.getMessage();
        }
    }

    @GetMapping("/list")
    public String listSubnets(@RequestParam String userId, Model model) {
        List<String> subnetList = subnetService.getUserSubnetList(userId);
        model.addAttribute("subnetList", subnetList);
        model.addAttribute("userId", userId);
        return "subnetList";  // subnet-list.html로 이동
    }
}
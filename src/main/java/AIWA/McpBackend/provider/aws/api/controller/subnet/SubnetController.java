package AIWA.McpBackend.provider.aws.api.controller.subnet;

import AIWA.McpBackend.provider.aws.api.dto.subnet.SubnetRequestDto;
import AIWA.McpBackend.service.subnet.SubnetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/subnet")
@RequiredArgsConstructor
public class SubnetController {

    private final SubnetService subnetService;

    @PostMapping("/create")
    public String createSubnet(@RequestBody SubnetRequestDto subnetRequest, @RequestParam String userId) {
        try {
            subnetService.createSubnet(subnetRequest, userId);
            return "Subnet 생성 요청이 성공적으로 처리되었습니다.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Subnet 생성 중 오류가 발생했습니다: " + e.getMessage();
        }
    }

    @DeleteMapping("/delete")
    public String deleteSubnet(@RequestParam String subnetName, @RequestParam String userId) {
        try {
            subnetService.deleteSubnet(subnetName, userId);
            return "Subnet 삭제 요청이 성공적으로 처리되었습니다.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Subnet 삭제 중 오류가 발생했습니다: " + e.getMessage();
        }
    }
}
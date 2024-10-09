package AIWA.McpBackend.provider.aws.api.controller.internetgateway;

import AIWA.McpBackend.provider.aws.api.dto.internetgateway.InternetGatewayRequestDto;
import AIWA.McpBackend.service.aws.internetgateway.InternetGatewayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internet-gateway")
@RequiredArgsConstructor
public class InternetGatewayController {

    private final InternetGatewayService internetGatewayService;

    @PostMapping("/create")
    public String createInternetGateway(@RequestBody InternetGatewayRequestDto igwRequest, @RequestParam String userId) {
        try {
            internetGatewayService.createInternetGateway(igwRequest, userId);
            return "Internet Gateway 생성 요청이 성공적으로 처리되었습니다.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Internet Gateway 생성 중 오류가 발생했습니다: " + e.getMessage();
        }
    }

    @DeleteMapping("/delete")
    public String deleteInternetGateway(@RequestParam String igwName, @RequestParam String userId) {
        try {
            internetGatewayService.deleteInternetGateway(igwName, userId);
            return "Internet Gateway 삭제 요청이 성공적으로 처리되었습니다.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Internet Gateway 삭제 중 오류가 발생했습니다: " + e.getMessage();
        }
    }
}
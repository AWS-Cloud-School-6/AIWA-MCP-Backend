package AIWA.McpBackend.provider.aws.api.controller.natgateway;

import AIWA.McpBackend.provider.aws.api.dto.natgateway.NatGatewayRequestDto;
import AIWA.McpBackend.service.aws.natgateway.NatGatewayService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/nat-gateway")
@RequiredArgsConstructor
public class NatGatewayController {

    private final NatGatewayService natGatewayService;

    @PostMapping("/create")
    public String createNatGateway(@RequestBody NatGatewayRequestDto natGatewayRequest, @RequestParam String userId) {
        try {
            natGatewayService.createNatGateway(natGatewayRequest, userId);
            return "NAT Gateway 생성 요청이 성공적으로 처리되었습니다.";
        } catch (Exception e) {
            e.printStackTrace();
            return "NAT Gateway 생성 중 오류가 발생했습니다: " + e.getMessage();
        }
    }

    @DeleteMapping("/delete")
    public String deleteNatGateway(@RequestParam String natGatewayName, @RequestParam String userId) {
        try {
            natGatewayService.deleteNatGateway(natGatewayName, userId);
            return "NAT Gateway 삭제 요청이 성공적으로 처리되었습니다.";
        } catch (Exception e) {
            e.printStackTrace();
            return "NAT Gateway 삭제 중 오류가 발생했습니다: " + e.getMessage();
        }
    }

    @PostMapping("/add-route")
    public String addRouteToNatGateway(@RequestParam String routeTableName, @RequestParam String natGatewayName, @RequestParam String cidrBlock, @RequestParam String userId) {
        try {
            natGatewayService.addRouteToNatGateway(routeTableName, natGatewayName, cidrBlock, userId);
            return "Route 추가 요청이 성공적으로 처리되었습니다.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Route 추가 중 오류가 발생했습니다: " + e.getMessage();
        }
    }
}
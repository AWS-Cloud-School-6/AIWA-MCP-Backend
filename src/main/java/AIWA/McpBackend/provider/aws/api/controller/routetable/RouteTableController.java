package AIWA.McpBackend.provider.aws.api.controller.routetable;

import AIWA.McpBackend.provider.aws.api.dto.routetable.RouteTableRequestDto;
import AIWA.McpBackend.service.routetable.RouteTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/route-table")
@RequiredArgsConstructor
public class RouteTableController {

    private final RouteTableService routeTableService;

    @PostMapping("/create")
    public String createRouteTable(@RequestBody RouteTableRequestDto routeTableRequest, @RequestParam String userId) {
        try {
            routeTableService.createRouteTable(routeTableRequest, userId);
            return "Route Table 생성 요청이 성공적으로 처리되었습니다.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Route Table 생성 중 오류가 발생했습니다: " + e.getMessage();
        }
    }

    @DeleteMapping("/delete")
    public String deleteRouteTable(@RequestParam String routeTableName, @RequestParam String userId) {
        try {
            routeTableService.deleteRouteTable(routeTableName, userId);
            return "Route Table 삭제 요청이 성공적으로 처리되었습니다.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Route Table 삭제 중 오류가 발생했습니다: " + e.getMessage();
        }
    }

    @PostMapping("/add-route")
    public String addRouteToInternetGateway(@RequestParam String routeTableName, @RequestParam String internetGatewayName, @RequestParam String cidrBlock, @RequestParam String userId) {
        try {
            routeTableService.addRouteToInternetGateway(routeTableName, internetGatewayName, cidrBlock, userId);
            return "Route 추가 요청이 성공적으로 처리되었습니다.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Route 추가 중 오류가 발생했습니다: " + e.getMessage();
        }
    }
}
package AIWA.McpBackend.provider.aws.api.controller.routetable;

import AIWA.McpBackend.provider.aws.api.dto.routetable.RouteAddRequestDto;
import AIWA.McpBackend.provider.aws.api.dto.routetable.RouteTableRequestDto;
import AIWA.McpBackend.provider.aws.api.dto.routetable.RouteTableSubnetAssociationRequestDto;
import AIWA.McpBackend.service.aws.routetable.RouteTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/route-table")
@RequiredArgsConstructor
public class RouteTableController {

    private final RouteTableService routeTableService;

    /**
     * 라우트 테이블 생성
     */
    @PostMapping("/create")
    public String createRouteTable(@RequestBody RouteTableRequestDto routeTableRequestDto) {
        try {
            routeTableService.createRouteTable(
                    routeTableRequestDto.getRouteTableName(),
                    routeTableRequestDto.getVpcName(),
                    routeTableRequestDto.getUserId()
            );
            return "Route Table 생성 요청이 성공적으로 처리되었습니다.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Route Table 생성 중 오류가 발생했습니다: " + e.getMessage();
        }
    }
    /**
     * 라우트 테이블에 게이트웨이 또는 엔드포인트로 라우트 추가
     */
    @PostMapping("/add-route")
    public String addRoute(@RequestBody RouteAddRequestDto routeAddRequestDto) {
        try {
            routeTableService.addRoute(
                    routeAddRequestDto.getRouteTableName(),
                    routeAddRequestDto.getDestinationCidr(),
                    routeAddRequestDto.getGatewayType(),
                    routeAddRequestDto.getGatewayId(),
                    routeAddRequestDto.getUserId()
            );
            return "Route 추가 요청이 성공적으로 처리되었습니다.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Route 추가 중 오류가 발생했습니다: " + e.getMessage();
        }
    }

    /**
     * 라우트 테이블을 서브넷과 연결
     */
    @PostMapping("/associate-subnet")
    public String associateRouteTableWithSubnet(@RequestBody RouteTableSubnetAssociationRequestDto requestDto) {
        try {
            routeTableService.associateRouteTableWithSubnet(
                    requestDto.getRouteTableName(),
                    requestDto.getSubnetName(),
                    requestDto.getUserId()
            );
            return "Route Table과 Subnet 연결이 성공적으로 처리되었습니다.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Route Table과 Subnet 연결 중 오류가 발생했습니다: " + e.getMessage();
        }
    }
}
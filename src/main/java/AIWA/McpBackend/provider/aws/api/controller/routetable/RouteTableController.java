package AIWA.McpBackend.provider.aws.api.controller.routetable;

import AIWA.McpBackend.provider.aws.api.dto.routetable.RouteAddRequestDto;
import AIWA.McpBackend.provider.aws.api.dto.routetable.RouteTableRequestDto;
import AIWA.McpBackend.provider.aws.api.dto.routetable.RouteTableSubnetAssociationRequestDto;
import AIWA.McpBackend.provider.response.CommonResult;
import AIWA.McpBackend.service.aws.routetable.RouteTableService;
import AIWA.McpBackend.service.response.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/route-table")
@RequiredArgsConstructor
public class RouteTableController {

    private final RouteTableService routeTableService;

    private final ResponseService responseService;

    /**
     * 라우트 테이블 생성
     */
    @PostMapping("/create")
    public CommonResult createRouteTable(@RequestBody RouteTableRequestDto routeTableRequestDto) {
        try {
            routeTableService.createRouteTable(
                    routeTableRequestDto.getRouteTableName(),
                    routeTableRequestDto.getVpcName(),
                    routeTableRequestDto.getUserId()
            );
            return responseService.getSuccessResult();
        } catch (Exception e) {
            e.printStackTrace();
            return responseService.getFailResult();
        }
    }
    /**
     * 라우트 테이블에 게이트웨이 또는 엔드포인트로 라우트 추가
     */
    @PostMapping("/add-route")
    public CommonResult addRoute(@RequestBody RouteAddRequestDto routeAddRequestDto) {
        try {
            routeTableService.addRoute(
                    routeAddRequestDto.getRouteTableName(),
                    routeAddRequestDto.getDestinationCidr(),
                    routeAddRequestDto.getGatewayType(),
                    routeAddRequestDto.getGatewayId(),
                    routeAddRequestDto.getUserId()
            );
            return responseService.getSuccessResult();
        } catch (Exception e) {
            e.printStackTrace();
            return responseService.getFailResult();
        }
    }

    /**
     * 라우트 테이블을 서브넷과 연결
     */
    @PostMapping("/associate-subnet")
    public CommonResult associateRouteTableWithSubnet(@RequestBody RouteTableSubnetAssociationRequestDto requestDto) {
        try {
            routeTableService.associateRouteTableWithSubnet(
                    requestDto.getRouteTableName(),
                    requestDto.getSubnetName(),
                    requestDto.getUserId()
            );
            return responseService.getSuccessResult();
        } catch (Exception e) {
            e.printStackTrace();
            return responseService.getFailResult();
        }
    }
}
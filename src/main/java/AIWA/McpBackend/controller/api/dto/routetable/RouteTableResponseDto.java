package AIWA.McpBackend.controller.api.dto.routetable;

import AIWA.McpBackend.controller.api.dto.subnet.SubnetResponseDto;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class RouteTableResponseDto {
    private String routeTableId;
    private String vpcId;
    private List<RouteDTO> routes;
    private Map<String, String> tags;
    private List<SubnetResponseDto> publicSubnets;  // 퍼블릭 서브넷 필드
    private List<SubnetResponseDto> privateSubnets; // 프라이빗 서브넷 필드

    public RouteTableResponseDto(String routeTableId, String vpcId, List<RouteDTO> routes, Map<String, String> tags,
                                 List<SubnetResponseDto> publicSubnets, List<SubnetResponseDto> privateSubnets) {


        this.routeTableId = routeTableId;
        this.vpcId = vpcId;
        this.routes = routes;
        this.tags = tags;
        this.publicSubnets = publicSubnets;
        this.privateSubnets = privateSubnets;
    }

    // Getters and Setters

}


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
    private List<SubnetResponseDto> associatedSubnets;

    public RouteTableResponseDto(String routeTableId, String vpcId, List<RouteDTO> routes, Map<String, String> tags, List<SubnetResponseDto> associatedSubnets) {
        this.routeTableId = routeTableId;
        this.vpcId = vpcId;
        this.routes = routes;
        this.tags = tags;
        this.associatedSubnets = associatedSubnets;
    }

    // Getters and Setters

}


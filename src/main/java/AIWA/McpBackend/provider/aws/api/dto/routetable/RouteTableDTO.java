package AIWA.McpBackend.provider.aws.api.dto.routetable;

import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class RouteTableDTO {
    private String routeTableId;
    private String vpcId;
    private List<RouteDTO> routes;
    private Map<String, String> tags;

    public RouteTableDTO(String routeTableId, String vpcId, List<RouteDTO> routes, Map<String, String> tags) {
        this.routeTableId = routeTableId;
        this.vpcId = vpcId;
        this.routes = routes;
        this.tags = tags;
    }

    // Getters and Setters

}

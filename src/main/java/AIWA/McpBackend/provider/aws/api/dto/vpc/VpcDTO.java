// VpcDTO.java
package AIWA.McpBackend.provider.aws.api.dto.vpc;

import AIWA.McpBackend.provider.aws.api.dto.routetable.RouteTableDTO;
import AIWA.McpBackend.provider.aws.api.dto.subnet.SubnetDTO;
import lombok.Getter;

import java.util.List;
import java.util.Map;

@Getter
public class VpcDTO {
    private final String vpcId;
    private final String cidr;
    private final Map<String, String> tags;
    private List<SubnetDTO> subnets; // 서브넷 리스트 추가
    private List<RouteTableDTO> routeTables; // 라우팅 테이블 리스트 추가


    public VpcDTO(String vpcId, String cidr, Map<String, String> tags, List<SubnetDTO> subnets, List<RouteTableDTO> routeTables) {
        this.vpcId = vpcId;
        this.cidr = cidr;
        this.tags = tags;
        this.subnets = subnets;
        this.routeTables = routeTables;
    }
}

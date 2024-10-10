package AIWA.McpBackend.provider.aws.api.dto.infra;

import AIWA.McpBackend.provider.aws.api.dto.subnet.SubnetRequestDto;
import AIWA.McpBackend.provider.aws.api.dto.vpc.VpcRequestDto;
import lombok.Data;

import java.util.List;

@Data
public class InfraRequestDto {
    private VpcRequestDto vpcRequest;
    private List<SubnetRequestDto> subnetRequests;
}
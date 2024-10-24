package AIWA.McpBackend.provider.aws;

import AIWA.McpBackend.provider.aws.api.dto.ec2.Ec2InstanceDTO;
import AIWA.McpBackend.provider.aws.api.dto.routetable.RouteTableResponseDto;
import AIWA.McpBackend.provider.aws.api.dto.securitygroup.SecurityGroupDTO;
import AIWA.McpBackend.provider.aws.api.dto.subnet.SubnetResponseDto;
import AIWA.McpBackend.provider.aws.api.dto.vpc.VpcTotalResponseDto;
import AIWA.McpBackend.service.aws.AwsResourceService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequiredArgsConstructor
public class AwsResourceController {

    private final AwsResourceService awsResourceService;

    @GetMapping("/api/aws/resources")
    public Map<String, Object> getAwsResources(@RequestParam String userId) {
        Map<String, Object> resources = new HashMap<>();
        awsResourceService.initializeClient(userId);

        // EC2 Instances
        List<Ec2InstanceDTO> ec2Instances = awsResourceService.fetchEc2Instances(userId);
        resources.put("ec2Instances", ec2Instances);

        // VPCs - 서브넷 및 라우팅 테이블 정보 전달
        List<VpcTotalResponseDto> vpcs = awsResourceService.fetchVpcs(userId);
        resources.put("vpcs", vpcs);

        // Security Groups
        List<SecurityGroupDTO> securityGroups = awsResourceService.fetchSecurityGroups(userId);
        resources.put("securityGroups", securityGroups);

        return resources; // 자동으로 JSON 형식으로 변환되어 응답
    }
}

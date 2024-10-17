package AIWA.McpBackend.provider.aws;

import AIWA.McpBackend.provider.aws.api.dto.ec2.Ec2InstanceDTO;
import AIWA.McpBackend.provider.aws.api.dto.routetable.RouteDTO;
import AIWA.McpBackend.provider.aws.api.dto.routetable.RouteTableDTO;
import AIWA.McpBackend.provider.aws.api.dto.securitygroup.SecurityGroupDTO;
import AIWA.McpBackend.provider.aws.api.dto.subnet.SubnetDTO;
import AIWA.McpBackend.provider.aws.api.dto.vpc.VpcDTO;
import AIWA.McpBackend.service.aws.AwsResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.ec2.model.Tag;

import java.util.*;
import java.util.stream.Collectors;

@RestController
public class AwsResourceController {

    private final AwsResourceService awsResourceService;

    @Autowired
    public AwsResourceController(AwsResourceService awsResourceService) {
        this.awsResourceService = awsResourceService;
    }

    @GetMapping("/api/aws/resources")
    public Map<String, Object> getAwsResources(@RequestParam String userId) {
        Map<String, Object> resources = new HashMap<>();
        awsResourceService.initializeClient(userId);

        // EC2 Instances
        List<Ec2InstanceDTO> ec2Instances = awsResourceService.fetchEc2Instances();
        resources.put("ec2Instances", ec2Instances);

        // Subnets
        List<SubnetDTO> subnets = awsResourceService.fetchSubnets();
        resources.put("subnets", subnets);

        // Route Tables
        List<RouteTableDTO> routeTables = awsResourceService.fetchRouteTables();
        resources.put("routeTables", routeTables);

        // VPCs - 서브넷 및 라우팅 테이블 정보 전달
        List<VpcDTO> vpcs = awsResourceService.fetchVpcs(subnets, routeTables);
        resources.put("vpcs", vpcs);

        // Security Groups
        List<SecurityGroupDTO> securityGroups = awsResourceService.fetchSecurityGroups();
        resources.put("securityGroups", securityGroups);

        return resources; // 자동으로 JSON 형식으로 변환되어 응답
    }
}

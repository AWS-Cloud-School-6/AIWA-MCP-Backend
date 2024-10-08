package AIWA.McpBackend.provider.aws.api.controller;

import AIWA.McpBackend.provider.aws.api.dto.ec2.Ec2InstanceDTO;
import AIWA.McpBackend.provider.aws.api.dto.securitygroup.SecurityGroupDTO;
import AIWA.McpBackend.provider.aws.api.dto.subnet.SubnetDTO;
import AIWA.McpBackend.provider.aws.api.dto.vpc.VpcDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class AwsResourceController {

    private final AwsResourceFetcher awsResourceFetcher;

    @Autowired
    public AwsResourceController(AwsResourceFetcher awsResourceFetcher) {
        this.awsResourceFetcher = awsResourceFetcher;
    }

    @GetMapping("/api/aws/resources/{email}")
    public Map<String, Object> getAwsResources(@PathVariable String email) {
        Map<String, Object> resources = new HashMap<>();
        awsResourceFetcher.initializeClient(email);

        // EC2 Instances
        List<Ec2InstanceDTO> ec2Instances = new ArrayList<>();
        awsResourceFetcher.fetchEc2Instances().forEach(reservation -> {
            reservation.instances().forEach(instance -> {
                // 인스턴스 상태를 문자열로 가져오기
                String instanceState = instance.state().name().toString(); // 상태를 문자열로 변환
                ec2Instances.add(new Ec2InstanceDTO(instance.instanceId(), instanceState));
            });
        });
        resources.put("ec2Instances", ec2Instances);

        // VPCs
        List<VpcDTO> vpcs = awsResourceFetcher.fetchVpcs().stream()
                .map(vpc -> new VpcDTO(vpc.vpcId(), vpc.cidrBlock()))
                .collect(Collectors.toList());
        resources.put("vpcs", vpcs);

        // Subnets
        List<SubnetDTO> subnets = awsResourceFetcher.fetchSubnets().stream()
                .map(subnet -> new SubnetDTO(subnet.subnetId(), subnet.cidrBlock()))
                .collect(Collectors.toList());
        resources.put("subnets", subnets);

        // Security Groups
        List<SecurityGroupDTO> securityGroups = awsResourceFetcher.fetchSecurityGroups().stream()
                .map(group -> new SecurityGroupDTO(group.groupId(), group.groupName()))
                .collect(Collectors.toList());
        resources.put("securityGroups", securityGroups);

        return resources; // 자동으로 JSON으로 변환되어 응답됩니다.
    }
}

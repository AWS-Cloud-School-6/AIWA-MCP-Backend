package AIWA.McpBackend.provider.aws.api.controller.awsResource;

import AIWA.McpBackend.provider.aws.api.dto.ec2.Ec2InstanceDTO;
import AIWA.McpBackend.provider.aws.api.dto.securitygroup.SecurityGroupDTO;
import AIWA.McpBackend.provider.aws.api.dto.subnet.SubnetDTO;
import AIWA.McpBackend.provider.aws.api.dto.vpc.VpcDTO;
import AIWA.McpBackend.service.awsResource.AwsResourceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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

    @GetMapping("/api/aws/resources/{email}")
    public Map<String, Object> getAwsResources(@PathVariable String email) {
        Map<String, Object> resources = new HashMap<>();
        awsResourceService.initializeClient(email);

// EC2 Instances
        List<Ec2InstanceDTO> ec2Instances = new ArrayList<>();
        awsResourceService.fetchEc2Instances().forEach(reservation -> {
            reservation.instances().forEach(instance -> {
                String instanceState = instance.state().name().toString();

                // 태그 정보를 Map으로 변환
                Map<String, String> tagsMap = instance.tags() == null ? Collections.emptyMap() :
                        instance.tags().stream().collect(Collectors.toMap(Tag::key, Tag::value));

                ec2Instances.add(new Ec2InstanceDTO(instance.instanceId(), instanceState, tagsMap));
            });
        });
        resources.put("ec2Instances", ec2Instances);

// VPCs
        List<VpcDTO> vpcs = awsResourceService.fetchVpcs().stream()
                .map(vpc -> {
                    Map<String, String> tagsMap = vpc.tags() == null ? Collections.emptyMap() :
                            vpc.tags().stream().collect(Collectors.toMap(Tag::key, Tag::value));
                    return new VpcDTO(vpc.vpcId(), vpc.cidrBlock(), tagsMap);
                })
                .collect(Collectors.toList());
        resources.put("vpcs", vpcs);

// Subnets
        List<SubnetDTO> subnets = awsResourceService.fetchSubnets().stream()
                .map(subnet -> {
                    Map<String, String> tagsMap = subnet.tags() == null ? Collections.emptyMap() :
                            subnet.tags().stream().collect(Collectors.toMap(Tag::key, Tag::value));
                    return new SubnetDTO(subnet.subnetId(), subnet.cidrBlock(), tagsMap);
                })
                .collect(Collectors.toList());
        resources.put("subnets", subnets);

// Security Groups
        List<SecurityGroupDTO> securityGroups = awsResourceService.fetchSecurityGroups().stream()
                .map(group -> {
                    Map<String, String> tagsMap = group.tags() == null ? Collections.emptyMap() :
                            group.tags().stream().collect(Collectors.toMap(Tag::key, Tag::value));
                    return new SecurityGroupDTO(group.groupId(), group.groupName(), tagsMap);
                })
                .collect(Collectors.toList());
        resources.put("securityGroups", securityGroups);

        return resources; // 자동으로 JSON 형식으로 변환되어 응답
    }
}

package AIWA.McpBackend.provider.aws;

import AIWA.McpBackend.provider.aws.api.dto.ec2.Ec2InstanceDTO;
import AIWA.McpBackend.provider.aws.api.dto.routetable.RouteTableDTO;
import AIWA.McpBackend.provider.aws.api.dto.securitygroup.SecurityGroupDTO;
import AIWA.McpBackend.provider.aws.api.dto.subnet.SubnetDTO;
import AIWA.McpBackend.provider.aws.api.dto.vpc.VpcDTO;
import AIWA.McpBackend.service.aws.AwsResourceService;
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

        // Routing Tables
        List<RouteTableDTO> routeTables = awsResourceService.fetchRouteTables().stream()
                .map(routeTable -> {
                    // Route 정보를 List<Map<String, String>> 형식으로 변환
                    List<Map<String, String>> routes = routeTable.routes().stream()
                            .map(route -> Map.of(
                                    "destinationCidrBlock", route.destinationCidrBlock(),
                                    "gatewayId", route.gatewayId()
                            ))
                            .collect(Collectors.toList());

                    // Tag 정보를 Map<String, String> 형식으로 변환
                    Map<String, String> tagsMap = routeTable.tags() == null ? Collections.emptyMap() :
                            routeTable.tags().stream().collect(Collectors.toMap(Tag::key, Tag::value));

                    // associations에서 첫 번째 association을 가져오고 VPC ID 가져오기
                    String vpcId = null;
                    if (!routeTable.associations().isEmpty()) {
                        String subnetId = routeTable.associations().get(0).subnetId(); // 서브넷 ID 가져오기
                        if (subnetId != null && !subnetId.isEmpty()) { // 서브넷 ID가 유효한 경우
                            vpcId = awsResourceService.getVpcIdFromSubnet(subnetId); // 서브넷 ID로 VPC ID 가져오기
                        } else {
                            // 서브넷 ID가 유효하지 않은 경우 로그를 남기거나 다른 처리를 할 수 있습니다.
                            System.out.println("Invalid subnet ID found in route table associations for route table ID: " + routeTable.routeTableId());
                        }
                    }

                    return new RouteTableDTO(routeTable.routeTableId(), vpcId, routes, tagsMap);
                })
                .collect(Collectors.toList());
        resources.put("routeTables", routeTables); // 라우팅 테이블 추가

        return resources; // 자동으로 JSON 형식으로 변환되어 응답
    }
}

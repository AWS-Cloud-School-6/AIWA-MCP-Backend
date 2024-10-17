package AIWA.McpBackend.service.aws;

import AIWA.McpBackend.entity.member.Member;
//import AIWA.McpBackend.service.kms.KmsService;
import AIWA.McpBackend.provider.aws.api.dto.ec2.Ec2InstanceDTO;
import AIWA.McpBackend.provider.aws.api.dto.routetable.RouteDTO;
import AIWA.McpBackend.provider.aws.api.dto.routetable.RouteTableDTO;
import AIWA.McpBackend.provider.aws.api.dto.securitygroup.SecurityGroupDTO;
import AIWA.McpBackend.provider.aws.api.dto.subnet.SubnetDTO;
import AIWA.McpBackend.provider.aws.api.dto.vpc.VpcDTO;
import AIWA.McpBackend.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.kms.KmsClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AwsResourceService {

    private Ec2Client ec2Client;

    private final MemberService memberService;

    public void initializeClient(String email) {
        // 특정 멤버의 AWS 자격 증명 가져오기
/*        Member member = memberService.getMemberById(memberId); // memberId로 Member 객체 조회*/
        Member member = memberService.getMemberByEmail(email);

        // AWS 자격 증명 생성
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(
                member.getAccess_key(),
                member.getSecret_key()
        );

        // EC2 클라이언트 생성
        this.ec2Client = Ec2Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .region(Region.of("ap-northeast-2")) // Member에서 리전 가져오기
                .build();
    }

    // EC2 Instances 가져오기
    public List<Ec2InstanceDTO> fetchEc2Instances() {
        DescribeInstancesRequest request = DescribeInstancesRequest.builder().build();
        DescribeInstancesResponse response = ec2Client.describeInstances(request);
        List<Ec2InstanceDTO> ec2Instances = new ArrayList<>();

        response.reservations().forEach(reservation -> {
            reservation.instances().forEach(instance -> {
                String instanceState = instance.state().nameAsString();
                Map<String, String> tagsMap = instance.tags() == null ? Collections.emptyMap() :
                        instance.tags().stream().collect(Collectors.toMap(Tag::key, Tag::value));
                ec2Instances.add(new Ec2InstanceDTO(instance.instanceId(), instanceState, tagsMap));
            });
        });

        return ec2Instances;
    }

    // Subnets 가져오기
    public List<SubnetDTO> fetchSubnets() {
        DescribeSubnetsRequest request = DescribeSubnetsRequest.builder().build();
        DescribeSubnetsResponse response = ec2Client.describeSubnets(request);
        return response.subnets().stream()
                .map(subnet -> {
                    Map<String, String> tagsMap = subnet.tags() == null ? Collections.emptyMap() :
                            subnet.tags().stream().collect(Collectors.toMap(Tag::key, Tag::value));
                    return new SubnetDTO(subnet.subnetId(), subnet.cidrBlock(), subnet.vpcId(), tagsMap, subnet.availabilityZone());
                })
                .collect(Collectors.toList());
    }

    // Route Tables 가져오기
    public List<RouteTableDTO> fetchRouteTables() {
        DescribeRouteTablesRequest request = DescribeRouteTablesRequest.builder().build();
        DescribeRouteTablesResponse response = ec2Client.describeRouteTables(request);
        return response.routeTables().stream()
                .map(routeTable -> {
                    Map<String, String> tagsMap = routeTable.tags() == null ? Collections.emptyMap() :
                            routeTable.tags().stream().collect(Collectors.toMap(Tag::key, Tag::value));
                    List<RouteDTO> routes = routeTable.routes().stream()
                            .map(route -> new RouteDTO(route.gatewayId(), route.destinationCidrBlock()))
                            .collect(Collectors.toList());
                    return new RouteTableDTO(routeTable.routeTableId(), routeTable.vpcId(), routes, tagsMap);
                })
                .collect(Collectors.toList());
    }

    // VPCs 가져오기
    public List<VpcDTO> fetchVpcs(List<SubnetDTO> subnets, List<RouteTableDTO> routeTables) {
        DescribeVpcsRequest request = DescribeVpcsRequest.builder().build();
        DescribeVpcsResponse response = ec2Client.describeVpcs(request);

        return response.vpcs().stream()
                .map(vpc -> {
                    Map<String, String> tagsMap = vpc.tags() == null ? Collections.emptyMap() :
                            vpc.tags().stream().collect(Collectors.toMap(Tag::key, Tag::value));

                    List<SubnetDTO> associatedSubnets = subnets.stream()
                            .filter(subnet -> subnet.getVpcId().equals(vpc.vpcId()))
                            .collect(Collectors.toList());

                    List<RouteTableDTO> associatedRouteTables = routeTables.stream()
                            .filter(routeTable -> routeTable.getVpcId().equals(vpc.vpcId()))
                            .collect(Collectors.toList());

                    return new VpcDTO(vpc.vpcId(), vpc.cidrBlock(), tagsMap, associatedSubnets, associatedRouteTables);
                })
                .collect(Collectors.toList());
    }

    // Security Groups 가져오기
    public List<SecurityGroupDTO> fetchSecurityGroups() {
        DescribeSecurityGroupsRequest request = DescribeSecurityGroupsRequest.builder().build();
        DescribeSecurityGroupsResponse response = ec2Client.describeSecurityGroups(request);
        return response.securityGroups().stream()
                .map(group -> {
                    Map<String, String> tagsMap = group.tags() == null ? Collections.emptyMap() :
                            group.tags().stream().collect(Collectors.toMap(Tag::key, Tag::value));
                    return new SecurityGroupDTO(group.groupId(), group.groupName(), tagsMap);
                })
                .collect(Collectors.toList());
    }

}

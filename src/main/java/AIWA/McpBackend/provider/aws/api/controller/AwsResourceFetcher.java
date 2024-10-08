package AIWA.McpBackend.provider.aws.api.controller;

import AIWA.McpBackend.entity.member.Member;
import AIWA.McpBackend.service.member.MemberService;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AwsResourceFetcher {

    private final Ec2Client ec2Client;

    public AwsResourceFetcher(@Value("${aws.accessKeyId}") String accessKey,
                              @Value("${aws.secretAccessKey}") String secretKey,
                              @Value("${aws.region}") String region) {
        AwsBasicCredentials awsCredentials = AwsBasicCredentials.create(accessKey, secretKey);
        this.ec2Client = Ec2Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(awsCredentials))
                .region(Region.of(region))
                .build();
    }

    public List<Reservation> fetchEc2Instances() {
        DescribeInstancesRequest request = DescribeInstancesRequest.builder().build();
        DescribeInstancesResponse response = ec2Client.describeInstances(request);
        return response.reservations();
    }

    public List<Vpc> fetchVpcs() {
        DescribeVpcsRequest request = DescribeVpcsRequest.builder().build();
        DescribeVpcsResponse response = ec2Client.describeVpcs(request);
        return response.vpcs();
    }

    public List<Subnet> fetchSubnets() {
        DescribeSubnetsRequest request = DescribeSubnetsRequest.builder().build();
        DescribeSubnetsResponse response = ec2Client.describeSubnets(request);
        return response.subnets();
    }

    public List<SecurityGroup> fetchSecurityGroups() {
        DescribeSecurityGroupsRequest request = DescribeSecurityGroupsRequest.builder().build();
        DescribeSecurityGroupsResponse response = ec2Client.describeSecurityGroups(request);
        return response.securityGroups();
    }
}

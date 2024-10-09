package AIWA.McpBackend.service.aws;

import AIWA.McpBackend.entity.member.Member;
import AIWA.McpBackend.service.member.MemberService;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AwsResourceService {

    private Ec2Client ec2Client;

    private final MemberService memberService;

    @Autowired
    public AwsResourceService(MemberService memberService) { // 생성자를 통한 주입
        this.memberService = memberService;
    }


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

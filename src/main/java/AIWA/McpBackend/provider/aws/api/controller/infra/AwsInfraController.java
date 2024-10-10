package AIWA.McpBackend.provider.aws.api.controller.infra;

import AIWA.McpBackend.provider.aws.api.dto.infra.InfraDeleteRequestDto;
import AIWA.McpBackend.provider.aws.api.dto.infra.InfraRequestDto;
import AIWA.McpBackend.provider.aws.api.dto.subnet.SubnetRequestDto;
import AIWA.McpBackend.service.aws.subnet.SubnetService;
import AIWA.McpBackend.service.aws.vpc.VpcService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/aws")
@RequiredArgsConstructor
public class AwsInfraController {

    private final VpcService vpcService;
    private final SubnetService subnetService;

    /**
     * VPC 및 Subnet을 생성합니다.
     *
     * @param infraRequest VPC 및 Subnet 생성 요청 DTO
     * @param userId 사용자 ID
     * @return 생성 결과 메시지
     */
    @PostMapping("/infra")
    public ResponseEntity<String> createVpcAndSubnets(@RequestBody InfraRequestDto infraRequest,
                                                      @RequestParam String userId) {
        try {
            // 1. VPC 생성
            vpcService.createVpc(infraRequest.getVpcRequest(), userId);

            // 2. Subnet 생성
            for (SubnetRequestDto subnetRequest : infraRequest.getSubnetRequests()) {
                subnetService.createSubnet(subnetRequest, userId);
            }

            return ResponseEntity.ok("VPC 및 Subnet 생성 성공");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("VPC 및 Subnet 생성 중 오류 발생: " + e.getMessage());
        }
    }

    /**
     * VPC 및 Subnet을 삭제합니다.
     *
     * @param infraDeleteRequest 삭제 요청 DTO
     * @param userId 사용자 ID
     * @return 삭제 결과 메시지
     */
    @DeleteMapping("/infra")
    public ResponseEntity<String> deleteVpcAndSubnets(@RequestBody InfraDeleteRequestDto infraDeleteRequest,
                                                      @RequestParam String userId) {
        try {
            // 1. Subnet 삭제
            for (String subnetName : infraDeleteRequest.getSubnetNames()) {
                subnetService.deleteSubnet(subnetName, userId);
            }

            // 2. VPC 삭제
            vpcService.deleteVpc(infraDeleteRequest.getVpcName(), userId);

            return ResponseEntity.ok("VPC 및 Subnet 삭제 성공");

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("VPC 및 Subnet 삭제 중 오류 발생: " + e.getMessage());
        }
    }
}
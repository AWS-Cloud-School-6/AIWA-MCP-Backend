package AIWA.McpBackend.provider.aws.api.controller.vpc;

import AIWA.McpBackend.provider.aws.api.dto.routetable.RouteDTO;
import AIWA.McpBackend.provider.aws.api.dto.routetable.RouteTableDTO;
import AIWA.McpBackend.provider.aws.api.dto.subnet.SubnetDTO;
import AIWA.McpBackend.provider.aws.api.dto.vpc.VpcDTO;
import AIWA.McpBackend.provider.aws.api.dto.vpc.VpcRequestDto;
import AIWA.McpBackend.service.aws.AwsResourceService;
import AIWA.McpBackend.service.aws.vpc.VpcService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.ec2.model.Tag;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vpc")
@RequiredArgsConstructor
public class VpcController {

    private final VpcService vpcService;
    private final AwsResourceService awsResourceService;

    /**
     * VPC 생성 엔드포인트
     *
     * @param vpcRequest VPC 생성 요청 DTO
     * @param userId     사용자 ID (요청 파라미터로 전달)
     * @return 생성 성공 메시지 또는 오류 메시지
     */
    @PostMapping("/create")
    public ResponseEntity<String> createVpc(
            @RequestBody VpcRequestDto vpcRequest,
            @RequestParam String userId) {
        try {
            vpcService.createVpc(vpcRequest, userId);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("VPC 생성 요청이 성공적으로 처리되었습니다.");
        } catch (Exception e) {
            // 예외 로그 기록 (추가적인 로깅 프레임워크 사용 권장)
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("VPC 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * VPC 삭제 엔드포인트
     *
     * @param vpcName VPC 이름 (요청 파라미터로 전달)
     * @param userId  사용자 ID (요청 파라미터로 전달)
     * @return 삭제 성공 메시지 또는 오류 메시지
     */
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteVpc(
            @RequestParam String vpcName,
            @RequestParam String userId) {
        try {
            vpcService.deleteVpc(vpcName, userId);
            return ResponseEntity.status(HttpStatus.OK)
                    .body("VPC 삭제 요청이 성공적으로 처리되었습니다.");
        } catch (Exception e) {
            // 예외 로그 기록 (추가적인 로깅 프레임워크 사용 권장)
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("VPC 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    @GetMapping("/")
    public Map<String, Object> getVpc(@RequestParam String userId) {

        Map<String, Object> resources = new HashMap<>();
        awsResourceService.initializeClient(userId);

        // Subnets
        List<SubnetDTO> subnets = awsResourceService.fetchSubnets();

        // Route Tables
        List<RouteTableDTO> routeTables = awsResourceService.fetchRouteTables();

        // VPCs - 서브넷 및 라우팅 테이블 정보 전달
        List<VpcDTO> vpcs = awsResourceService.fetchVpcs(subnets, routeTables);
        resources.put("vpcs", vpcs);

        return resources;
    }
}
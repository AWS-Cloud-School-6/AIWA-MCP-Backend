package AIWA.McpBackend.provider.aws.api.controller.vpc;

import AIWA.McpBackend.provider.aws.api.dto.vpc.VpcRequestDto;
import AIWA.McpBackend.service.vpc.VpcService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vpc")
@RequiredArgsConstructor
public class VpcController {

    private final VpcService vpcService;

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
}
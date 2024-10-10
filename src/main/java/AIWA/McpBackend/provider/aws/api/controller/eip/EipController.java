package AIWA.McpBackend.provider.aws.api.controller.eip;

import AIWA.McpBackend.provider.aws.api.dto.eip.EipRequestDto;
import AIWA.McpBackend.service.aws.eip.EipService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/eip")
@RequiredArgsConstructor
public class EipController {

    private final EipService eipService;

    /**
     * EIP 생성 엔드포인트
     *
     * @param eipRequestDto 요청 DTO (사용자 ID, EC2 인스턴스 ID 포함)
     * @return 성공 또는 오류 메시지
     */
    @PostMapping("/create")
    public ResponseEntity<String> createEip(@RequestBody EipRequestDto eipRequestDto) {
        try {
            eipService.createEip(eipRequestDto.getUserId(), eipRequestDto.getInstanceId());
            return ResponseEntity.ok("EIP 생성 요청이 성공적으로 처리되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("EIP 생성 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    /**
     * EIP 삭제 엔드포인트
     *
     * @param eipRequestDto 요청 DTO (사용자 ID, EIP ID 포함)
     * @return 성공 또는 오류 메시지
     */
    @PostMapping("/delete")
    public ResponseEntity<String> deleteEip(@RequestBody EipRequestDto eipRequestDto) {
        try {
            eipService.deleteEip(eipRequestDto.getUserId(), eipRequestDto.getEipId());
            return ResponseEntity.ok("EIP 삭제 요청이 성공적으로 처리되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("EIP 삭제 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
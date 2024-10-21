package AIWA.McpBackend.provider.aws.api.controller.eip;

import AIWA.McpBackend.provider.aws.api.dto.eip.EipRequestDto;
import AIWA.McpBackend.provider.response.CommonResult;
import AIWA.McpBackend.service.aws.eip.EipService;
import AIWA.McpBackend.service.response.ResponseService;
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

    private final ResponseService responseService;

    /**
     * EIP 생성 엔드포인트
     *
     * @param eipRequestDto 요청 DTO (사용자 ID, EC2 인스턴스 ID 포함)
     * @return 성공 또는 오류 메시지
     */
    @PostMapping("/create")
    public CommonResult createEip(@RequestBody EipRequestDto eipRequestDto) {
        try {
            eipService.createEip(eipRequestDto.getUserId(), eipRequestDto.getInstanceId());
            return responseService.getSuccessResult();
        } catch (Exception e) {
            e.printStackTrace();
            return responseService.getFailResult();
        }
    }

    /**
     * EIP 삭제 엔드포인트
     *
     * @param eipRequestDto 요청 DTO (사용자 ID, EIP ID 포함)
     * @return 성공 또는 오류 메시지
     */
    @PostMapping("/delete")
    public CommonResult deleteEip(@RequestBody EipRequestDto eipRequestDto) {
        try {
            eipService.deleteEip(eipRequestDto.getUserId(), eipRequestDto.getEipId());
            return responseService.getSuccessResult();
        } catch (Exception e) {
            e.printStackTrace();
            return responseService.getFailResult();
        }
    }
}
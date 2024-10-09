package AIWA.McpBackend.provider.aws.api.controller.vpc;

import AIWA.McpBackend.provider.aws.api.dto.vpc.VpcRequestDto;
import AIWA.McpBackend.service.vpc.VpcService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vpc")
@RequiredArgsConstructor
public class VpcController {

    private final VpcService vpcService;

    @PostMapping("/create")
    public String createVpc(@RequestBody VpcRequestDto vpcRequest, @RequestParam String userId) {
        try {
            vpcService.createVpc(vpcRequest, userId);
            return "VPC 생성 요청이 성공적으로 처리되었습니다.";
        } catch (Exception e) {
            e.printStackTrace();
            return "VPC 생성 중 오류가 발생했습니다: " + e.getMessage();
        }
    }
}
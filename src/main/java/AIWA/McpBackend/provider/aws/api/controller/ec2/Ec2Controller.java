package AIWA.McpBackend.provider.aws.api.controller.ec2;

import AIWA.McpBackend.provider.aws.api.dto.ec2.Ec2RequestDto;
import AIWA.McpBackend.service.aws.ec2.Ec2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ec2")
@RequiredArgsConstructor
public class Ec2Controller {

    private final Ec2Service ec2InstanceService;

    @PostMapping("/create")
    public String createEC2Instance(@RequestBody Ec2RequestDto instanceRequest, @RequestParam String userId) {
        try {
            ec2InstanceService.createEC2Instance(instanceRequest, userId);
            return "EC2 인스턴스 생성 요청이 성공적으로 처리되었습니다.";
        } catch (Exception e) {
            e.printStackTrace();
            return "EC2 인스턴스 생성 중 오류가 발생했습니다: " + e.getMessage();
        }
    }

    @DeleteMapping("/delete")
    public String deleteEC2Instance(@RequestParam String instanceName, @RequestParam String userId) {
        try {
            ec2InstanceService.deleteEC2Instance(instanceName, userId);
            return "EC2 인스턴스 삭제 요청이 성공적으로 처리되었습니다.";
        } catch (Exception e) {
            e.printStackTrace();
            return "EC2 인스턴스 삭제 중 오류가 발생했습니다: " + e.getMessage();
        }
    }
}
package AIWA.McpBackend.provider.aws.api.controller.ec2;

import AIWA.McpBackend.provider.aws.api.dto.ec2.Ec2RequestDto;
import AIWA.McpBackend.provider.response.CommonResult;
import AIWA.McpBackend.service.aws.ec2.Ec2Service;
import AIWA.McpBackend.service.response.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ec2")
@RequiredArgsConstructor
public class Ec2Controller {

    private final Ec2Service ec2InstanceService;

    private final ResponseService responseService;

    @PostMapping("/create")
    public CommonResult createEC2Instance(@RequestBody Ec2RequestDto instanceRequest, @RequestParam String userId) {
        try {
            ec2InstanceService.createEC2Instance(instanceRequest, userId);
            return responseService.getSuccessResult();
        } catch (Exception e) {
            e.printStackTrace();
            return responseService.getFailResult();
        }
    }

    @DeleteMapping("/delete")
    public CommonResult deleteEC2Instance(@RequestParam String instanceName, @RequestParam String userId) {
        try {
            ec2InstanceService.deleteEC2Instance(instanceName, userId);
            return responseService.getSuccessResult();
        } catch (Exception e) {
            e.printStackTrace();
            return responseService.getFailResult();
        }
    }
}
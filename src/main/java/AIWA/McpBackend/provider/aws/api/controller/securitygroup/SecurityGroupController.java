package AIWA.McpBackend.provider.aws.api.controller.securitygroup;

import AIWA.McpBackend.provider.aws.api.dto.securitygroup.SecurityGroupRequestDto;
import AIWA.McpBackend.provider.response.CommonResult;
import AIWA.McpBackend.service.aws.securitygroup.SecurityGroupService;
import AIWA.McpBackend.service.response.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/security-group")
@RequiredArgsConstructor
public class SecurityGroupController {

    private final SecurityGroupService securityGroupService;

    private final ResponseService responseService;

    @PostMapping("/create")
    public CommonResult createSecurityGroup(@RequestBody SecurityGroupRequestDto securityGroupRequest, @RequestParam String userId) {
        try {
            securityGroupService.createSecurityGroup(securityGroupRequest, userId);
            return responseService.getSuccessResult();
        } catch (Exception e) {
            e.printStackTrace();
            return responseService.getFailResult();
        }
    }

    @DeleteMapping("/delete")
    public CommonResult deleteSecurityGroup(@RequestParam String securityGroupName, @RequestParam String userId) {
        try {
            securityGroupService.deleteSecurityGroup(securityGroupName, userId);
            return responseService.getSuccessResult();
        } catch (Exception e) {
            e.printStackTrace();
            return responseService.getFailResult();
        }
    }
}

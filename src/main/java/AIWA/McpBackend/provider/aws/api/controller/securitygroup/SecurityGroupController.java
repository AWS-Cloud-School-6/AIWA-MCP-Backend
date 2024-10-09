package AIWA.McpBackend.provider.aws.api.controller.securitygroup;

import AIWA.McpBackend.provider.aws.api.dto.securitygroup.SecurityGroupRequestDto;
import AIWA.McpBackend.service.securitygroup.SecurityGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/security-group")
@RequiredArgsConstructor
public class SecurityGroupController {

    private final SecurityGroupService securityGroupService;

    @PostMapping("/create")
    public String createSecurityGroup(@RequestBody SecurityGroupRequestDto securityGroupRequest, @RequestParam String userId) {
        try {
            securityGroupService.createSecurityGroup(securityGroupRequest, userId);
            return "Security Group 생성 요청이 성공적으로 처리되었습니다.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Security Group 생성 중 오류가 발생했습니다: " + e.getMessage();
        }
    }

    @DeleteMapping("/delete")
    public String deleteSecurityGroup(@RequestParam String securityGroupName, @RequestParam String userId) {
        try {
            securityGroupService.deleteSecurityGroup(securityGroupName, userId);
            return "Security Group 삭제 요청이 성공적으로 처리되었습니다.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Security Group 삭제 중 오류가 발생했습니다: " + e.getMessage();
        }
    }
}

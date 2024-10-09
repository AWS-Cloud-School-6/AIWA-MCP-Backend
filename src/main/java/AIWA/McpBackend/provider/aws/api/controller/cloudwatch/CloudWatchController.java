package AIWA.McpBackend.provider.aws.api.controller.cloudwatch;

import AIWA.McpBackend.provider.aws.api.dto.cloudwatch.CloudWatchAlarmRequestDto;
import AIWA.McpBackend.service.cloudwatch.CloudWatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cloudwatch")
@RequiredArgsConstructor
public class CloudWatchController {

    private final CloudWatchService cloudWatchService;

    @PostMapping("/create-alarm")
    public String createCloudWatchAlarm(@RequestBody CloudWatchAlarmRequestDto alarmRequest, @RequestParam String userId) {
        try {
            cloudWatchService.createCloudWatchAlarm(alarmRequest, userId);
            return "CloudWatch 알람 생성 요청이 성공적으로 처리되었습니다.";
        } catch (Exception e) {
            e.printStackTrace();
            return "CloudWatch 알람 생성 중 오류가 발생했습니다: " + e.getMessage();
        }
    }

    @DeleteMapping("/delete-alarm")
    public String deleteCloudWatchAlarm(@RequestParam String alarmName, @RequestParam String userId) {
        try {
            cloudWatchService.deleteCloudWatchAlarm(alarmName, userId);
            return "CloudWatch 알람 삭제 요청이 성공적으로 처리되었습니다.";
        } catch (Exception e) {
            e.printStackTrace();
            return "CloudWatch 알람 삭제 중 오류가 발생했습니다: " + e.getMessage();
        }
    }
}
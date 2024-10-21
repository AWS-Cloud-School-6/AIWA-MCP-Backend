package AIWA.McpBackend.provider.aws.api.controller.internetgateway;

import AIWA.McpBackend.provider.aws.api.dto.internetgateway.InternetGatewayRequestDto;
import AIWA.McpBackend.provider.response.CommonResult;
import AIWA.McpBackend.service.aws.internetgateway.InternetGatewayService;
import AIWA.McpBackend.service.response.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/internet-gateway")
@RequiredArgsConstructor
public class InternetGatewayController {

    private final InternetGatewayService internetGatewayService;

    private final ResponseService responseService;

    @PostMapping("/create")
    public CommonResult createInternetGateway(@RequestBody InternetGatewayRequestDto igwRequest, @RequestParam String userId) {
        try {
            internetGatewayService.createInternetGateway(igwRequest, userId);
            return responseService.getSuccessResult();
        } catch (Exception e) {
            e.printStackTrace();
            return responseService.getFailResult();
        }
    }

    @DeleteMapping("/delete")
    public CommonResult deleteInternetGateway(@RequestParam String igwName, @RequestParam String userId) {
        try {
            internetGatewayService.deleteInternetGateway(igwName, userId);
            return responseService.getSuccessResult();
        } catch (Exception e) {
            e.printStackTrace();
            return responseService.getFailResult();
        }
    }
}
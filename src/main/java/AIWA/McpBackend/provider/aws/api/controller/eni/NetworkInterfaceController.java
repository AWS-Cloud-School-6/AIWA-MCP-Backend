package AIWA.McpBackend.provider.aws.api.controller.eni;

import AIWA.McpBackend.provider.aws.api.dto.eni.NetworkInterfaceDto;
import AIWA.McpBackend.provider.response.ListResult;
import AIWA.McpBackend.service.aws.AwsResourceService;
import AIWA.McpBackend.service.response.ResponseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/network-interface")
@RequiredArgsConstructor
public class NetworkInterfaceController {

    private final AwsResourceService awsResourceService;
    private final ResponseService responseService;

    @GetMapping("/describe")
    public ListResult<NetworkInterfaceDto> listNetworkInterfaces(@RequestParam String userId) {
        List<NetworkInterfaceDto> networkInterfaces = awsResourceService.fetchNetworkInterfaces(userId);
        return responseService.getListResult(networkInterfaces);
    }
}
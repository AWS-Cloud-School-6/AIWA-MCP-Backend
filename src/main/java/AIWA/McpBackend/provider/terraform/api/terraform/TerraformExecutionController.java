package AIWA.McpBackend.provider.terraform.api.terraform;

import AIWA.McpBackend.service.terraform.TerraformExecutionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/terraform")
public class TerraformExecutionController {

    private final TerraformExecutionService terraformExecutionService;

    /**
     * Terraform 작업을 실행합니다.
     */
    @PostMapping("/execute")
    public ResponseEntity<String> execute(@RequestBody Map<String, String> files) {
        try {
            String tfStateContent = terraformExecutionService.executeTerraform(files);
            return ResponseEntity.ok(tfStateContent);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Terraform 작업 실패: " + e.getMessage());
        }
    }
}
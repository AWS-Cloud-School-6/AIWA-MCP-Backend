package AIWA.McpBackend.api.controller;

import AIWA.McpBackend.api.dto.ec2.Ec2RequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ec2")
@RequiredArgsConstructor
public class Ec2Controller {

    @PostMapping("/create")
    public ResponseEntity<String> createEC2Instance(@RequestBody Ec2RequestDto ec2Request) {
        try {
            String result = ec2Service.createEC2Instance(ec2Request);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to create EC2 instance: " + e.getMessage());
        }
    }
}

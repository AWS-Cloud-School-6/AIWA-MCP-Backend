package AIWA.McpBackend.provider.aws.api.dto.ec2;

import lombok.Data;

import java.util.Map;

@Data
public class Ec2Request {
    private String instanceName;
    private String amiId;
    private String instanceType;
    private String keyName;
    private String subnetId;
    private Map<String, String> tags;

    // getters and setters
}
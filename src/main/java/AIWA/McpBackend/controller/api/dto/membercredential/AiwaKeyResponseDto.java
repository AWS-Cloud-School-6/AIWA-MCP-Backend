package AIWA.McpBackend.controller.api.dto.membercredential;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AiwaKeyResponseDto {

    private String companyName;   // 회사 이름 (AWS, GCP 등)
    private String accessKey;     // Access Key (AWS의 경우만 있을 수 있음)
    private String secretKey;     // Secret Key (AWS의 경우만 있을 수 있음)
    private String projectId;
    private String gcpKeyPath;    // GCP Key Path (GCP의 경우만 있을 수 있음)
    private String awsTfvarsUrl;
    private String gcpTfvarsUrl;

}

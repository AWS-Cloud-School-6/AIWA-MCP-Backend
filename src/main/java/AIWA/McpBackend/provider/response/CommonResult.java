package AIWA.McpBackend.provider.response;

import lombok.Data;

@Data
public class CommonResult { // api 실행 결과를 담는 공통 모델
    private boolean success;
    private int code;
    private String msg;

}
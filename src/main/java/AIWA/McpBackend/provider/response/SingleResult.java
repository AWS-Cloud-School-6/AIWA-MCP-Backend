package AIWA.McpBackend.provider.response;

import lombok.Data;

@Data
public class SingleResult<T>extends CommonResult { //결과가 단일건인 API를 담는 모델
    private T data;
}

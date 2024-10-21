package AIWA.McpBackend.provider.response;

import lombok.Data;
import lombok.Getter;

import java.util.List;

@Getter
public class ListResult<T> extends CommonResult{
    private List<T> list;
}

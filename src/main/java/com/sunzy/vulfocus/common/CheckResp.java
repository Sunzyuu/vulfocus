package com.sunzy.vulfocus.common;

import com.github.dockerjava.api.model.Container;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
/**
 * 检测容器的运行状态封装返回值
 */
public class CheckResp {
    /**
     * 是否处于runnin状态
     */
    private boolean flag;
    /**
     * 容器对象
     */
    private Container container;

}

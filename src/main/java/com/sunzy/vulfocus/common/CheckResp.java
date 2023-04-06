package com.sunzy.vulfocus.common;

import com.github.dockerjava.api.model.Container;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class CheckResp {
    private boolean flag;
    private Container container;

}

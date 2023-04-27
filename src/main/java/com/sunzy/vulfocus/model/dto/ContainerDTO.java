package com.sunzy.vulfocus.model.dto;

import com.sunzy.vulfocus.model.po.ContainerVul;
import lombok.Data;

@Data
public class ContainerDTO extends ContainerVul {
    String username;
    String vulName;
    String vulDesc;
}

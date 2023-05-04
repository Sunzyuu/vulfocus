package com.sunzy.vulfocus.model.dto;

import lombok.Data;

@Data
public class TimeTempDTO {
    private String tempId;
    private Integer userId;
    private String desc;
    private String timeDesc;
    private Integer timeRange;
    private String imageName;
    private Boolean flagStatus;
    private String timeImgType;
    private String rankRange;
}

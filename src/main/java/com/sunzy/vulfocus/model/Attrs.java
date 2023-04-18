package com.sunzy.vulfocus.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Attrs {
    @TableId
    private String id;

    private String name;

    private String vul_name;

    private String imagePort;

    private String desc;

    private Double rank;

    private Double port;

    @TableField("is_ok")
    private Boolean ok;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;

    @TableField("is_share")
    private Boolean share;

    private String degree;

    private String isStatus;
}

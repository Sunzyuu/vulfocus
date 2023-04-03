package com.sunzy.vulfocus.model.po;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author sunzy
 * @since 2023-04-01
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class ContainerVul implements Serializable {

    private static final long serialVersionUID = 1L;

    private String containerId;

    private String dockerContainerId;

    private Integer userId;

    private String vulHost;

    private String containerStatus;

    private String containerPort;

    private String vulPort;

    private String containerFlag;

    private LocalDateTime createDate;

    @TableField("is_check")
    private Boolean iScheck;

    private LocalDateTime isCheckDate;

    private String timeModelId;

    private String imageIdId;


}

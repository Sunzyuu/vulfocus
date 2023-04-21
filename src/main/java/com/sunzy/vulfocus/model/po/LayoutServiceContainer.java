package com.sunzy.vulfocus.model.po;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author sunzy
 * @since 2023-04-17
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class LayoutServiceContainer implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId
    private String serviceContainerId;

    private Integer userId;

    private String dockerContainerId;

    private String containerHost;

    private String containerStatus;

    private String containerPort;

    private String containerFlag;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;

    private String imageId;

    private String layoutUserId;

    private String serviceId;


}

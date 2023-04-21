package com.sunzy.vulfocus.model.po;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
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
public class LayoutService implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId
    private String serviceId;

    @TableField("is_exposed")
    private Boolean exposed;

    private String exposedSourcePort;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;

    private String imageId;

    private String layoutId;

    private String serviceName;


}

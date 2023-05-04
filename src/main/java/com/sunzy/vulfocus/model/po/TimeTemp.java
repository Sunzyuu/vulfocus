package com.sunzy.vulfocus.model.po;

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
 * @since 2023-05-04
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class TimeTemp implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId
    private String tempId;

    private Integer userId;

    private Integer timeRange;

    private String imageName;

    private String timeDesc;

    private String timeImgType;

    private String rankRange;

    private Boolean flagStatus;


}

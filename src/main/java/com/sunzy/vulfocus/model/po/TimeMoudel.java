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
public class TimeMoudel implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId
    private String timeId;

    private Integer userId;

    private Long startTime;

    private Long endTime;

    private Boolean status;

    private String tempTimeIdId;


}

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
public class TimeRank implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId
    private String rankId;

    private Integer userId;

    private String name;

    private Double rank;

    private String timeTempId;


}

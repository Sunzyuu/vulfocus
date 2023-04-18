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
public class LayoutServiceContainerScore implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId
    private String layoutServiceContainerScoreId;

    private Integer userId;

    private String flag;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;

    private String layoutDataIdId;

    private String layoutIdId;

    private String serviceContainerIdId;

    private String imageIdId;

    private String serviceIdId;


}

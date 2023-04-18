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
public class LayoutData implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId
    private String layoutUserId;

    private Integer createUserId;

    private String status;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;

    private String layoutIdId;

    private String filePath;


}

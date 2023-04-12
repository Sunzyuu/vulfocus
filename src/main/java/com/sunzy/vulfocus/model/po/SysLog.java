package com.sunzy.vulfocus.model.po;

import java.time.LocalDateTime;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
public class SysLog implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId
    private String logId;

    private Integer userId;

    private String operationType;

    private String operationName;

    private String operationValue;

    private String operationArgs;

    private String ip;

    private LocalDateTime createDate;


}

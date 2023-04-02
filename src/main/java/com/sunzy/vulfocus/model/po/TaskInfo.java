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
public class TaskInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String taskId;

    private Integer userId;

    private String taskName;

    private Integer taskStatus;

    private LocalDateTime taskStartDate;

    private LocalDateTime taskEndDate;

    private String operationType;

    private String operationArgs;

    private String taskMsg;

    @TableField("is_show")
    private Boolean show;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;


}

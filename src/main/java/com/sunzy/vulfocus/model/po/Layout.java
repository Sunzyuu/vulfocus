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
public class Layout implements Serializable {

    private static final long serialVersionUID = 1L;
    @TableId
    private String layoutId;

    private String layoutName;

    private Integer createUserId;

    private String ymlContent;

    private String envContent;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;

    private String layoutDesc;

    private String rawContent;

    private String imageName;

//    @TableField("is_release")
    private Boolean isRelease;


}

package com.sunzy.vulfocus.model.po;

import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableField;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
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
@TableName("`image_info`")
@EqualsAndHashCode(callSuper = false)
public class ImageInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String imageId;

    private String imageName;

    private String imageVulName;

    private String imagePort;

    private String imageDesc;

    private Double ranks;

    @TableField("is_ok")
    private Boolean ok;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;

    @TableField("is_share")
    private Boolean share;

    private String degree;

    private String isStatus;


}

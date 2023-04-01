package com.sunzy.vulfocus.model.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
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
public class UserUserprofile implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String password;

    private LocalDateTime lastLogin;

    @TableField("is_superuser")
    private Boolean superuser;

    private String username;

    private String firstName;

    private String lastName;

    private String email;

    @TableField("is_staff")
    private Boolean staff;

    @TableField("is_active")
    private Boolean active;

    private LocalDateTime dateJoined;

    private String avatar;

    private String role;


}

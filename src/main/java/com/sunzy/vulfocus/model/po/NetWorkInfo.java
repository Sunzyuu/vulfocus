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
 * @since 2023-04-14
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class NetWorkInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId
    private String netWorkId;

    private String netWorkClientId;

    private Integer createUser;

    private String netWorkName;

    private String netWorkSubnet;

    private String netWorkGateway;

    private String netWorkScope;

    private String netWorkDriver;

    private Boolean enableIpv6;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;


}

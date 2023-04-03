package com.sunzy.vulfocus.service;

import com.sunzy.vulfocus.model.dto.UserDTO;
import com.sunzy.vulfocus.model.po.ContainerVul;
import com.sunzy.vulfocus.model.po.ImageInfo;
import com.sunzy.vulfocus.model.po.SysLog;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author sunzy
 * @since 2023-04-01
 */
public interface SysLogService extends IService<SysLog> {

    public void sysImageLog(UserDTO user, ImageInfo info, String operationName);

    public void sysContainerLog(UserDTO user, ContainerVul containerVul, String operationName);

}

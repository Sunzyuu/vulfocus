package com.sunzy.vulfocus.service;

import com.sunzy.vulfocus.common.Result;
import com.sunzy.vulfocus.model.dto.UserDTO;
import com.sunzy.vulfocus.model.po.ContainerVul;
import com.sunzy.vulfocus.model.po.ImageInfo;
import com.sunzy.vulfocus.model.po.Layout;
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

    Result getSysLog(int currentPage, String data);

    Result getConfig();

    void sysImageLog(UserDTO user, ImageInfo info, String operationName);

    void sysLayoutLog(UserDTO user, Layout layout, String operationName);

    void sysTimeLog(UserDTO user, String operationName);

    void sysContainerLog(UserDTO user, ImageInfo imageInfo, ContainerVul containerVul, String operationName);

    void sysFlagLog(UserDTO user, String vulName, String operationName, String flag);

    void sysLayoutFlagLog(UserDTO user, String operationValue, String operationName, String flag);
}

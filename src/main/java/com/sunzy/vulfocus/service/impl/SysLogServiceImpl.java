package com.sunzy.vulfocus.service.impl;

import com.sunzy.vulfocus.model.dto.UserDTO;
import com.sunzy.vulfocus.model.po.ContainerVul;
import com.sunzy.vulfocus.model.po.ImageInfo;
import com.sunzy.vulfocus.model.po.SysLog;
import com.sunzy.vulfocus.mapper.SysLogMapper;
import com.sunzy.vulfocus.service.SysLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author sunzy
 * @since 2023-04-01
 */
@Service
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, SysLog> implements SysLogService {
    private static final String OPERATION_TYPE_IMAGE = "镜像";
    private static final String OPERATION_TYPE_CONTAINER = "容器";
    private static final String OPERATION_TYPE_USER = "用户";

    @Override
    public void sysImageLog(UserDTO user, ImageInfo imageInfo, String operationName) {
        SysLog sysLog = new SysLog();
        sysLog.setOperationType(OPERATION_TYPE_IMAGE);
        sysLog.setUserId(user.getId());
        sysLog.setOperationName(operationName);
        sysLog.setIp(user.getRequestIp());
        sysLog.setCreateDate(LocalDateTime.now());
        // TODO
        sysLog.setOperationValue(imageInfo.getImageName());
        sysLog.setOperationArgs(imageInfo.getImageName());
        save(sysLog);
    }

    @Override
    public void sysContainerLog(UserDTO user, ContainerVul containerVul, String operationName) {
        SysLog sysLog = new SysLog();
        sysLog.setOperationType(OPERATION_TYPE_CONTAINER);
        sysLog.setUserId(user.getId());
        sysLog.setOperationName(operationName);
        sysLog.setIp(user.getRequestIp());
        sysLog.setCreateDate(LocalDateTime.now());
        // TODO
        sysLog.setOperationValue(containerVul.getImageIdId());
        sysLog.setOperationArgs(containerVul.getImageIdId());
        save(sysLog);
    }


}

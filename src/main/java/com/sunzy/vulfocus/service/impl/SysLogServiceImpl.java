package com.sunzy.vulfocus.service.impl;

import com.alibaba.fastjson.JSON;
import com.sunzy.vulfocus.model.dto.ImageDTO;
import com.sunzy.vulfocus.model.dto.UserDTO;
import com.sunzy.vulfocus.model.po.ContainerVul;
import com.sunzy.vulfocus.model.po.ImageInfo;
import com.sunzy.vulfocus.model.po.SysLog;
import com.sunzy.vulfocus.mapper.SysLogMapper;
import com.sunzy.vulfocus.service.SysLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunzy.vulfocus.utils.GetIpUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;

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

    @Resource
    private ImageInfoServiceImpl imageInfoService;
    @Override
    public void sysImageLog(UserDTO user, ImageInfo imageInfo, String operationName) {
        SysLog sysLog = new SysLog();
        sysLog.setLogId(GetIpUtils.getUUID());
        sysLog.setOperationType(OPERATION_TYPE_IMAGE);
        sysLog.setUserId(user.getId());
        sysLog.setOperationName(operationName);
        sysLog.setIp(user.getRequestIp());
        sysLog.setCreateDate(LocalDateTime.now());
        // TODO
        ImageDTO imageDTO;
        String args = "{}";
        try {
            imageDTO = imageInfoService.handleImageDTO(imageInfo, user);
            args = JSON.toJSONString(imageDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
        sysLog.setOperationValue(imageInfo.getImageName());
        sysLog.setOperationArgs(args);
        save(sysLog);
    }

    @Override
    public void sysContainerLog(UserDTO user,ImageInfo imageInfo, ContainerVul containerVul, String operationName) {
        SysLog sysLog = new SysLog();
        sysLog.setLogId(GetIpUtils.getUUID());
        sysLog.setOperationType(OPERATION_TYPE_CONTAINER);
        sysLog.setUserId(user.getId());
        sysLog.setOperationName(operationName);
        sysLog.setIp(user.getRequestIp());
        sysLog.setCreateDate(LocalDateTime.now());
        // TODO
        String imageVulName = imageInfo.getImageVulName();
        sysLog.setOperationValue(imageVulName);
        sysLog.setOperationArgs(JSON.toJSONString(imageInfo));
        save(sysLog);
    }


}

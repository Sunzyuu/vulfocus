package com.sunzy.vulfocus.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sunzy.vulfocus.common.Result;
import com.sunzy.vulfocus.common.SystemConstants;
import com.sunzy.vulfocus.model.dto.ImageDTO;
import com.sunzy.vulfocus.model.dto.SysLogDTO;
import com.sunzy.vulfocus.model.dto.UserDTO;
import com.sunzy.vulfocus.model.po.ContainerVul;
import com.sunzy.vulfocus.model.po.ImageInfo;
import com.sunzy.vulfocus.model.po.SysLog;
import com.sunzy.vulfocus.mapper.SysLogMapper;
import com.sunzy.vulfocus.model.po.UserUserprofile;
import com.sunzy.vulfocus.service.SysLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunzy.vulfocus.service.UserUserprofileService;
import com.sunzy.vulfocus.utils.GetConfig;
import com.sunzy.vulfocus.utils.GetIdUtils;
import com.sunzy.vulfocus.utils.UserHolder;
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

    @Resource
    private UserUserprofileService userService;

    @Override
    public Result getSysLog(int currentPage) {
        UserDTO user = UserHolder.getUser();
        if(user.getSuperuser()){
            Page page = new Page(currentPage, SystemConstants.PAGE_SIZE);
            page(page);
            return Result.ok(page);
        } else {
            return Result.ok();
        }
    }

    @Override
    public Result getConfig() {
        return Result.ok(GetConfig.get());
    }

    @Override
    public void sysImageLog(UserDTO user, ImageInfo imageInfo, String operationName) {
        SysLog sysLog = new SysLog();
        sysLog.setLogId(GetIdUtils.getUUID());
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
        sysLog.setLogId(GetIdUtils.getUUID());
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

    @Override
    public void sysFlagLog(UserDTO user, String vulName, String operationName, String flag) {
        SysLog sysLog = new SysLog();
        sysLog.setLogId(GetIdUtils.getUUID());
        sysLog.setOperationType(OPERATION_TYPE_CONTAINER);
        sysLog.setUserId(user.getId());
        sysLog.setOperationName(operationName);
        sysLog.setIp(user.getRequestIp());
        sysLog.setCreateDate(LocalDateTime.now());
        sysLog.setOperationValue(vulName);
        sysLog.setOperationArgs(JSON.toJSONString(flag));
        save(sysLog);
    }

    private SysLogDTO handlerSysLog(SysLog sysLog){
        SysLogDTO logDTO = new SysLogDTO();
        Integer userId = sysLog.getUserId();
        UserUserprofile user = userService.getById(userId);
        logDTO.setUsername(user.getUsername());
        logDTO.setOperationName(sysLog.getOperationName());
        logDTO.setOperationValue(sysLog.getOperationValue());
        logDTO.setOperationArgs(sysLog.getOperationArgs());
        logDTO.setOperationType(sysLog.getOperationType());
        logDTO.setIp(sysLog.getIp());
        logDTO.setCreatedDate(sysLog.getCreateDate());
        return logDTO;
    }


}

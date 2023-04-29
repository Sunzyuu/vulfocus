package com.sunzy.vulfocus.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sunzy.vulfocus.common.Result;
import com.sunzy.vulfocus.common.SystemConstants;
import com.sunzy.vulfocus.model.dto.ImageDTO;
import com.sunzy.vulfocus.model.dto.SysLogDTO;
import com.sunzy.vulfocus.model.dto.UserDTO;
import com.sunzy.vulfocus.model.po.*;
import com.sunzy.vulfocus.mapper.SysLogMapper;
import com.sunzy.vulfocus.service.SysLogService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunzy.vulfocus.service.UserUserprofileService;
import com.sunzy.vulfocus.utils.GetConfig;
import com.sunzy.vulfocus.utils.Utils;
import com.sunzy.vulfocus.utils.UserHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author sunzy
 * @since 2023-04-01
 */
@Service
@Transactional
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, SysLog> implements SysLogService {
    private static final String OPERATION_TYPE_IMAGE = "镜像";
    private static final String OPERATION_TYPE_CONTAINER = "容器";
    private static final String OPERATION_TYPE_LAYOUT = "编排环境";
    private static final String OPERATION_TYPE_USER = "用户";

    @Resource
    private ImageInfoServiceImpl imageInfoService;

    @Resource
    private UserUserprofileService userService;

    @Override
    public Result getSysLog(int currentPage, String data) {
        UserDTO user = UserHolder.getUser();
        if(user.getSuperuser()){
            Page page = new Page<SysLog>(currentPage, SystemConstants.PAGE_SIZE);
            LambdaQueryWrapper<SysLog> queryWrapper = new LambdaQueryWrapper<>();
            if(StrUtil.isNotEmpty(data)){
                queryWrapper.like(true, SysLog::getOperationName, data).or()
                        .like(true, SysLog::getOperationArgs, data).or()
                        .like(true, SysLog::getOperationType, data).or()
                        .like(true, SysLog::getOperationValue, data).or()
                        .like(true, SysLog::getOperationName, data).or()
                        .like(true, SysLog::getIp, data);
            }
            this.page(page,queryWrapper);
            List sysLogList = page.getRecords();
            ArrayList<SysLogDTO> sysLogDTOS = new ArrayList<>();
            for (Object sysLog : sysLogList) {
                sysLogDTOS.add(handlerSysLog((SysLog) sysLog));
            }

            page.setRecords(sysLogDTOS);
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
        sysLog.setLogId(Utils.getUUID());
        sysLog.setOperationType(OPERATION_TYPE_IMAGE);
        sysLog.setUserId(user.getId());
        sysLog.setOperationName(operationName);
        sysLog.setIp(user.getRequestIp());
        sysLog.setCreateDate(LocalDateTime.now());
        ImageDTO imageDTO;
        String args = "{}";
        try {
            imageDTO = imageInfoService.handlerImageDTO(imageInfo, user);
            args = JSON.toJSONString(imageDTO);
        } catch (Exception e) {
            e.printStackTrace();
        }
        sysLog.setOperationValue(imageInfo.getImageName());
        sysLog.setOperationArgs(args);
        saveOrUpdate(sysLog);
    }

    @Override
    public void sysLayoutLog(UserDTO user, Layout layout, String operationName) {
        SysLog sysLog = new SysLog();
        sysLog.setLogId(Utils.getUUID());
        sysLog.setOperationType(OPERATION_TYPE_LAYOUT);
        sysLog.setUserId(user.getId());
        sysLog.setOperationName(operationName);
        sysLog.setIp(user.getRequestIp());
        sysLog.setCreateDate(LocalDateTime.now());
        sysLog.setOperationArgs(JSON.toJSONString(layout));
        sysLog.setOperationValue(layout.getLayoutName());
        save(sysLog);
    }

    @Override
    public void sysContainerLog(UserDTO user,ImageInfo imageInfo, ContainerVul containerVul, String operationName) {
        SysLog sysLog = new SysLog();
        sysLog.setLogId(Utils.getUUID());
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
        sysLog.setLogId(Utils.getUUID());
        sysLog.setOperationType(OPERATION_TYPE_CONTAINER);
        sysLog.setUserId(user.getId());
        sysLog.setOperationName(operationName);
        sysLog.setIp(user.getRequestIp());
        sysLog.setCreateDate(LocalDateTime.now());
        sysLog.setOperationValue(vulName);
        sysLog.setOperationArgs(JSON.toJSONString(flag));
        save(sysLog);
    }

    @Override
    public void sysLayoutFlagLog(UserDTO user, String operationValue, String operationName, String flag) {
        SysLog sysLog = new SysLog();
        sysLog.setLogId(Utils.getUUID());
        sysLog.setOperationType(OPERATION_TYPE_LAYOUT);
        sysLog.setUserId(user.getId());
        sysLog.setOperationName(operationName);
        sysLog.setIp(user.getRequestIp());
        sysLog.setCreateDate(LocalDateTime.now());
        sysLog.setOperationValue(operationValue);
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

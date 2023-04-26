package com.sunzy.vulfocus.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sunzy.vulfocus.common.Result;
import com.sunzy.vulfocus.model.dto.UserDTO;
import com.sunzy.vulfocus.model.dto.Vulnerability;
import com.sunzy.vulfocus.model.po.ContainerVul;
import com.sunzy.vulfocus.mapper.ContainerVulMapper;
import com.sunzy.vulfocus.model.po.ImageInfo;
import com.sunzy.vulfocus.model.po.UserUserprofile;
import com.sunzy.vulfocus.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunzy.vulfocus.utils.UserHolder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
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
public class ContainerVulServiceImpl extends ServiceImpl<ContainerVulMapper, ContainerVul> implements ContainerVulService {

    @Resource
    private ImageInfoService imageService;

    @Resource
    private UserUserprofileService userService;

    @Resource
    private SysLogService logService;

    @Resource
    private TaskInfoService taskService;

    @Override
    public Result getContainers(String flag, int page, String imageId) {
        UserDTO user = UserHolder.getUser();
        // TODO 时间模式检测
        Page<ContainerVul> containerVulPage = new Page<>();

        if("list".equals(flag) && user.getSuperuser()){
            if(!StrUtil.isBlank(imageId)){
                LambdaQueryWrapper<ContainerVul> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(true, ContainerVul::getImageIdId, imageId);
                queryWrapper.orderBy(true, false, ContainerVul::getCreateDate);
                page(containerVulPage,queryWrapper);
            } else {
                LambdaQueryWrapper<ContainerVul> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(true, ContainerVul::getUserId, user.getId());
                page(containerVulPage,queryWrapper);
            }
        }
        return Result.ok(containerVulPage);
    }

    /**
     * 校验flag
     * @param flag
     * @param containerId
     * @return
     */
    @Override
    public Result checkFlag(String flag, String containerId) {
        UserDTO user = UserHolder.getUser();
        ContainerVul containerVul = query().eq("container_id", containerId).one();
        if(containerVul == null){
            return Result.fail("参数错误！");
        }
        Vulnerability vulnerability = handlerVulnerability(containerVul, user);

        logService.sysFlagLog(user, vulnerability.getVulName(), "提交flag", flag);
        if(user.getId() != containerVul.getUserId()){
            return Result.build("Flag 与用户不匹配", null);
        } else if(flag == null){
            return Result.build("Flag 不能为空", null);
        } else if(!flag.equals(containerVul.getContainerFlag())){
            return Result.build("Flag 错误", null);
        } else {
            if(!containerVul.getIScheck()){
                containerVul.setIsCheckDate(LocalDateTime.now());
                containerVul.setIScheck(true);
                LambdaQueryWrapper<ContainerVul> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(true,ContainerVul::getContainerId, containerId);
                update(containerVul, wrapper);
            }
            // TODO 时间模式检测
            try {
                taskService.deleteContainerTask(containerVul, user);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return Result.ok();
        }

    }

    @Override
    public Result startContainer(String containerId) {
        if(StrUtil.isBlank(containerId)){
            return Result.fail("容器id不能为空！");
        }
        UserDTO user = UserHolder.getUser();
        ContainerVul containerVul = query().eq("container_id", containerId).one();
        if(containerVul == null){
            return Result.fail("环境不存在！");
        }
        String taskId = null;
        try {
            taskId = taskService.createContainerTask(containerVul, user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.ok(taskId);
    }

    @Override
    public Result stopContainer(String containerId) {
        if(StrUtil.isBlank(containerId)){
            return Result.fail("容器id不能为空！");
        }
        UserDTO user = UserHolder.getUser();
        ContainerVul containerVul = query().eq("container_id", containerId).one();
        if(containerVul == null){
            return Result.fail("环境不存在！");
        }
        String taskId = null;
        try {
            taskId = taskService.stopContainerTask(containerVul, user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.ok(taskId);
    }

    @Override
    public Result deleteContainer(String containerId) {
        if(StrUtil.isBlank(containerId)){
            return Result.fail("容器id不能为空！");
        }
        UserDTO user = UserHolder.getUser();
        ContainerVul containerVul = query().eq("container_id", containerId).one();
        if(containerVul == null){
            return Result.fail("环境不存在！");
        }
        String taskId = null;
        try {
            taskId = taskService.deleteContainerTask(containerVul, user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.ok(taskId);
    }


    /**
     * 序列化容器信息
     * @param containerVul
     * @param userDTO
     * @return
     */
    private Vulnerability handlerVulnerability(ContainerVul containerVul, UserDTO userDTO){
        String imageId = containerVul.getImageIdId();
        ImageInfo imageInfo = imageService.query().eq("image_id", imageId).one();
        UserUserprofile user = userService.getById(userDTO.getId());
        Vulnerability vulnerability = new Vulnerability();
        vulnerability.setContainerId(containerVul.getContainerId());
        vulnerability.setImageId(imageId);
        vulnerability.setName(imageInfo.getImageName());
        vulnerability.setVulDesc(imageInfo.getImageDesc());
        vulnerability.setVulHost(containerVul.getVulHost());
        vulnerability.setVulName(imageInfo.getImageVulName());
        vulnerability.setCheck(containerVul.getIScheck());
        vulnerability.setUsername(user.getUsername());
        vulnerability.setIsCheckDate(containerVul.getIsCheckDate());
        return vulnerability;
    }
}

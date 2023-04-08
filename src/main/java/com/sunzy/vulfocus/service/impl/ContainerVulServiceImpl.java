package com.sunzy.vulfocus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sunzy.vulfocus.common.Result;
import com.sunzy.vulfocus.model.dto.UserDTO;
import com.sunzy.vulfocus.model.dto.UserInfo;
import com.sunzy.vulfocus.model.dto.Vulnerability;
import com.sunzy.vulfocus.model.po.ContainerVul;
import com.sunzy.vulfocus.mapper.ContainerVulMapper;
import com.sunzy.vulfocus.model.po.ImageInfo;
import com.sunzy.vulfocus.model.po.UserUserprofile;
import com.sunzy.vulfocus.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunzy.vulfocus.utils.UserHolder;
import org.apache.catalina.User;
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

package com.sunzy.vulfocus.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.dockerjava.api.model.Image;
import com.sunzy.vulfocus.common.ErrorClass;
import com.sunzy.vulfocus.common.Result;
import com.sunzy.vulfocus.common.SystemConstants;
import com.sunzy.vulfocus.model.dto.CreateImage;
import com.sunzy.vulfocus.model.dto.ImageDTO;
import com.sunzy.vulfocus.model.dto.UserDTO;
import com.sunzy.vulfocus.model.po.*;
import com.sunzy.vulfocus.mapper.ImageInfoMapper;
import com.sunzy.vulfocus.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunzy.vulfocus.utils.DockerTools;
import com.sunzy.vulfocus.utils.Utils;
import com.sunzy.vulfocus.utils.UserHolder;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author sunzy
 * @since 2023-04-01
 */
@Service
@Transactional
public class ImageInfoServiceImpl extends ServiceImpl<ImageInfoMapper, ImageInfo> implements ImageInfoService {

    @Resource
    private ImageInfoMapper imageInfoMapper;

    @Resource
    private UserUserprofileService userService;

    @Resource
    private TaskInfoService taskService;

    @Resource
    private SysLogService logService;

    @Resource
    private ContainerVulService containerService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private TimeMoudelServiceImpl timeMoudelService;


    @Override
    public Result getLocalImages() {
        /**
         * data
         * :
         * [{name: "alpine:latest", flag: false, image_id: "9ed4aefc74"},…]
         * 0
         * :
         * {name: "alpine:latest", flag: false, image_id: "9ed4aefc74"}
         * 1
         * :
         * {name: "wurstmeister/zookeeper:latest", flag: false, image_id: "3f43f72cb2"}
         * msg
         * :
         * "OK"
         * status
         * :
         * 200
         */
        List<Image> images = DockerTools.imageList();

        LambdaQueryWrapper<ImageInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(true, ImageInfo::getOk, true);
        List<ImageInfo> imageInfos = imageInfoMapper.selectList(wrapper);

        ArrayList<String> nameList = new ArrayList<>();
        for (ImageInfo imageInfo : imageInfos) {
            nameList.add(imageInfo.getImageName());
        }
        ArrayList<LocalImage> localImages = new ArrayList<>();
        for (Image image : images) {
            for (String tag : image.getRepoTags()) {
                LocalImage localImage = new LocalImage();
                localImage.setName(tag);
                localImage.setFlag(false);
                if (nameList.contains(tag)) {
                    localImage.setFlag(true);
                }
                localImage.setId(getImageId(image.getId()).substring(0, 10));
                localImages.add(localImage);
            }
        }
        return Result.ok(localImages);
    }

    @Override
    public Result getImageList(String query, int page, String flag) throws Exception {
        UserDTO user = UserHolder.getUser();
        Page<ImageInfo> imageInfoPage = new Page<>(page, SystemConstants.PAGE_SIZE);
        LambdaQueryWrapper<ImageInfo> wrapper = new LambdaQueryWrapper<>();
//        long timeNow = Utils.dataTimeToTimestamp(LocalDateTime.now());
//        TimeMoudel data = timeMoudelService.query().eq("user_id", user.getId()).ge("end_time", timeNow).one();
        if (user.getSuperuser()) {
            if (!"".equals(query)) {
                query = query.trim();
                if (!"".equals(flag) && "flag".equals(flag)) {
                    wrapper.like(!"".equals(query), ImageInfo::getImageName, query);
                    wrapper.like(!"".equals(query), ImageInfo::getImageDesc, query);
                    wrapper.like(!"".equals(query), ImageInfo::getImageVulName, query);
                    wrapper.orderBy(true, false, ImageInfo::getCreateDate);
                    page(imageInfoPage, wrapper);
                    return handlerPage(imageInfoPage, user);
                } else {
                    wrapper.like(!"".equals(query), ImageInfo::getImageName, query);
                    wrapper.like(!"".equals(query), ImageInfo::getImageDesc, query);
                    wrapper.like(!"".equals(query), ImageInfo::getImageVulName, query);
                    wrapper.eq(true, ImageInfo::getOk, true);
                    wrapper.orderBy(true, false, ImageInfo::getCreateDate);
                    page(imageInfoPage, wrapper);
                    return handlerPage(imageInfoPage, user);
                }
            } else {
                if (!"".equals(flag) && "flag".equals(flag)) {
                    wrapper.eq(true, ImageInfo::getOk, true);
                    page(imageInfoPage, wrapper);
                    return handlerPage(imageInfoPage, user);
                } else {
                    wrapper.eq(true, ImageInfo::getOk, true);
                    wrapper.orderBy(true, false, ImageInfo::getCreateDate);
                    page(imageInfoPage, wrapper);
//                    List<String> imageStringList = stringRedisTemplate.opsForList()
//                            .range("cache:image:" + page, 0, -1);
//                    if(imageStringList == null || imageStringList.size() == 0){
//                        wrapper.eq(true, ImageInfo::getOk, true);
//                        wrapper.orderBy(true, false, ImageInfo::getCreateDate);
//                        page(imageInfoPage, wrapper);
//                        // 向redis存数据
//                        List<ImageInfo> imageInfoList = imageInfoPage.getRecords();
//                        for (ImageInfo imageInfo : imageInfoList) {
//                            stringRedisTemplate.opsForList().rightPush("cache:image:" + page, JSON.toJSONString(imageInfo));
//                        }
//                    } else {
//                        List<ImageInfo> imageInfoList = new ArrayList<>();
//                        // 从redis中取数据并解析
//                        for (String imageString : imageStringList) {
//                            ImageInfo imageInfo = JSON.parseObject(imageString, ImageInfo.class);
//                            imageInfoList.add(imageInfo);
//                        }
//                        imageInfoPage.setRecords(imageInfoList);
//                        imageInfoPage.setTotal(count());
//                    }
                    return handlerPage(imageInfoPage, user);
                }
            }
        } else {
            // 普通用户
            if (!"".equals(query)) {
                query = query.trim();
                wrapper.like(!"".equals(query), ImageInfo::getImageName, query);
                wrapper.like(!"".equals(query), ImageInfo::getImageDesc, query);
                wrapper.like(!"".equals(query), ImageInfo::getImageVulName, query);
                wrapper.eq(true, ImageInfo::getOk, true);
                wrapper.orderBy(true, false, ImageInfo::getCreateDate);
                page(imageInfoPage, wrapper);
                return handlerPage(imageInfoPage, user);
            } else {
                wrapper.eq(true, ImageInfo::getOk, true);
                wrapper.orderBy(true, false, ImageInfo::getCreateDate);
                page(imageInfoPage, wrapper);
                return handlerPage(imageInfoPage, user);
            }
        }
    }

    /**
     * 通过文件创建镜像
     *
     * @param createImage 前端传入的参数
     * @return
     */
    @Override
    public Result createImage(CreateImage createImage) {
        UserDTO user = UserHolder.getUser();
        String imageName = !createImage.getImageName().equals("") ? createImage.getImageName() : "";
        String imageVulName = !createImage.getImageVulName().equals("") ? createImage.getImageVulName() : "";
        String imageDesc = !createImage.getImageDesc().equals("") ? createImage.getImageDesc() : "";
        double rank = createImage.getRank() == 0 ? (float) 2.5 : createImage.getRank();
        MultipartFile file = createImage.getFile();
        String path = "";
        File tmpFile = null;
        if (file != null) {
            String imageFileName = file.getOriginalFilename();
            path = SystemConstants.DOCKERFILE_UPLOAD_DIR + "/" + imageFileName;
            tmpFile = new File(path);
            if (tmpFile.exists()) {
                return Result.ok("该文件已存在");
            }
            //保存文件
            try {
                BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(path));
                outputStream.write(file.getBytes());
                outputStream.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println(imageFileName);
        }

        ImageInfo imageInfo = null;
        if (!StrUtil.isBlank(imageName)) {
            if (!imageName.contains(":")) {
                imageName = imageName + ":latest";
            }
            imageInfo = query().eq("image_name", imageName).one();
        } else {
            return Result.fail("镜像文件或镜像名称不能为空");
        }

        if (imageInfo == null) {
            imageInfo = new ImageInfo();
            imageInfo.setImageId(Utils.getUUID());
            imageInfo.setImageName(imageName);
            imageInfo.setImageVulName(imageVulName);
            imageInfo.setImageDesc(imageDesc);
            imageInfo.setRank(rank);
//            imageInfo.setOk(false);
            imageInfo.setCreateDate(LocalDateTime.now());
            imageInfo.setUpdateDate(LocalDateTime.now());
            imageInfo.setOk(false);
            if (file == null) {
                save(imageInfo);
            }
        }
        // create taskInfo
        String taskId = taskService.createImageTask(imageInfo, user, tmpFile);
        if (file != null) {
            TaskInfo taskInfo = taskService.getById(taskId);
            return Result.ok(taskInfo.getTaskMsg());
        }
        String msg = "pull image " + imageName + " successfully!";
        return Result.ok(msg, taskId);
    }

    /**
     * 修改镜像信息
     *
     * @param imageDTO 镜像
     * @return
     */
    @Override
    public Result editImage(ImageDTO imageDTO) {
        UserDTO user = UserHolder.getUser();
        if (!user.getSuperuser()) {
            return Result.build("权限不足", null);
        }
        if (StrUtil.isBlank(imageDTO.getImageId())) {
            return Result.fail("参数不能为空");
        }
        ImageInfo imageInfo = query().eq("image_id", imageDTO.getImageId()).one();
        if (imageInfo == null) {
            return Result.build("镜像不存在", null);
        }

        if (imageDTO.getRank() != null) {
            imageInfo.setRank(imageDTO.getRank());
        }
        if (!StrUtil.isBlank(imageDTO.getImageVulName())) {
            imageInfo.setImageVulName(imageDTO.getImageVulName());
        }
        if (!StrUtil.isBlank(imageDTO.getImageDesc())) {
            imageInfo.setImageDesc(imageDTO.getImageDesc());
        }
        imageInfo.setUpdateDate(LocalDateTime.now());
        updateById(imageInfo);
        return Result.ok();
    }

    @Override
    public Result downloadImage(String imageId) {
        UserDTO user = UserHolder.getUser();
        if (!user.getSuperuser()) {
            return Result.fail("权限不足");
        }
        ImageInfo imageInfo = null;
        imageInfo = getById(imageId);
        if (imageInfo == null) {
            return Result.fail("镜像不存在");
        }
        String taskId = taskService.createImageTask(imageInfo, user, null);
        return Result.ok(taskId);
    }

    @Override
    public Result deleteImage(String imageId) throws Exception {
        UserDTO user = UserHolder.getUser();
        if (!user.getSuperuser()) {
            return Result.fail("权限不足");
        }
        ImageInfo imageInfo = query().eq("image_id", imageId).one();
        if (imageInfo == null) {
            return Result.ok();
        }
        logService.sysImageLog(user, imageInfo, "删除");
        imageId = imageInfo.getImageId();
        LambdaQueryWrapper<ContainerVul> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(true, ContainerVul::getImageIdId, imageId);
        List<ContainerVul> containerVulList = containerService.list(queryWrapper);
        if (containerVulList.size() == 0) {
            LambdaQueryWrapper<ImageInfo> deleteWrapper = new LambdaQueryWrapper<>();
            deleteWrapper.eq(true, ImageInfo::getImageId, imageId);
            boolean isSuccess = remove(deleteWrapper);
            if (isSuccess) {
                return Result.ok();
            } else {
                return Result.fail("删除失败！");
            }
        } else {
            return Result.build("镜像正在使用，无法删除！", JSON.toJSONString(containerVulList));
        }
    }

    /**
     * 批量导入本地镜像信息
     *
     * @param imageNamesStr
     * @return
     */
    @Override
    public Result batchLocalAdd(String imageNamesStr) {
        UserDTO user = UserHolder.getUser();
        if (!user.getSuperuser()) {
            return Result.fail("权限不足");
        }
        if (StrUtil.isBlank(imageNamesStr)) {
            return Result.ok();
        }
        List<String> resp = new ArrayList<>();

        String[] imageNames = imageNamesStr.split(",");
        for (String imageName : imageNames) {
            if (StrUtil.isBlank(imageName)) {
                continue;
            }
            if (!imageName.contains(":latest")) {
                imageName = imageName + ":latest";
            }
            ImageInfo imageInfo = query().eq("image_name", imageName).one();
            if (imageInfo == null) {
                String imageVulName = imageName.split(":")[0];
                imageInfo = new ImageInfo();
                imageInfo.setImageId(Utils.getUUID());
                imageInfo.setImageName(imageName);
                imageInfo.setImageVulName(imageVulName);
                imageInfo.setImageDesc(imageName);
                imageInfo.setOk(false);
                imageInfo.setRank(2.5);
                imageInfo.setCreateDate(LocalDateTime.now());
                imageInfo.setUpdateDate(LocalDateTime.now());
                save(imageInfo);
            }

            String taskId = taskService.createImageTask(imageInfo, user, null);
            if (!StrUtil.isBlank(taskId)) {
                resp.add("拉取镜像" + imageName + "任务下发成功");
            }
        }
        return Result.ok(resp);
    }

    /**
     * 从镜像创建容器
     *
     * @param imageId 镜像id
     * @return
     */
    @Override
    public Result startContainer(String imageId) {
        UserDTO user = UserHolder.getUser();
        ImageInfo imageInfo = query().eq("image_id", imageId).one();
        if (imageInfo == null) {
            return Result.fail("镜像不存在");
        }
        LambdaQueryWrapper<ContainerVul> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(true, ContainerVul::getImageIdId, imageId);
        queryWrapper.eq(true, ContainerVul::getUserId, user.getId());
        ContainerVul containerVul = containerService.getOne(queryWrapper);
        if (containerVul == null) {
            containerVul = new ContainerVul();
            containerVul.setContainerId(Utils.getUUID());
            containerVul.setImageIdId(imageId);
            containerVul.setUserId(user.getId());
            containerVul.setContainerPort(imageInfo.getImagePort());
            containerVul.setVulHost("");
            containerVul.setIScheck(false);
            containerVul.setContainerStatus("stop");
            containerVul.setDockerContainerId("");
            containerVul.setVulPort("");
            containerVul.setCreateDate(LocalDateTime.now());
            containerVul.setContainerFlag("");
            containerService.save(containerVul);
        }
        String taskId = null;
        try {
            taskId = taskService.createContainerTask(containerVul, user);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.ok("", taskId);
    }


    @Override
    public ImageDTO handlerImageDTO(ImageInfo imageInfo, UserDTO user) throws Exception {
        ImageDTO imageDTO = new ImageDTO();
        BeanUtils.copyProperties(imageInfo, imageDTO);
        Integer userId = null;
        if (user != null) {
            userId = user.getId();
        }
        Map<String, Object> status = new HashMap<>();
        // 查询该用户创建的容器
        LambdaQueryWrapper<ContainerVul> wraper = new LambdaQueryWrapper<>();
        wraper.eq(true, ContainerVul::getUserId, userId);
        wraper.eq(true, ContainerVul::getImageIdId, imageInfo.getImageId());
        ContainerVul data = null;
        try {
            data = containerService.getOne(wraper);
        } catch (Exception e) {
            throw ErrorClass.ContainerNotOneException;
        }
        status.put("status", "");
        status.put("is_check", false);
        status.put("container_id", "");
        status.put("start_date", "");
        status.put("end_date", "");
        status.put("host", "");
        status.put("port", "");
        status.put("progress", 0.0);
        status.put("progress_status", "");
        if (data != null) {
            status.put("start_date", "");
            status.put("end_date", "");
            if (data.getDockerContainerId() == null || data.getDockerContainerId().equals("")) {
                data.setContainerStatus("delete");
            }
            if (data.getContainerStatus().equals("running")) {
                status.put("host", data.getVulHost());
                status.put("port", data.getVulPort());
                Map<String, Object> operationArgs = new HashMap<>();
                operationArgs.put("image_name", imageInfo.getImageName());
                operationArgs.put("user_id", userId);
                operationArgs.put("image_port", imageInfo.getImagePort());
                LambdaQueryWrapper<TaskInfo> taskInfoQueryWrapper = new LambdaQueryWrapper<>();
                taskInfoQueryWrapper.eq(true, TaskInfo::getUserId, userId);
                taskInfoQueryWrapper.eq(true, TaskInfo::getTaskStatus, 3);
                taskInfoQueryWrapper.eq(true, TaskInfo::getOperationType, 2);
                taskInfoQueryWrapper.eq(true, TaskInfo::getOperationArgs, JSON.toJSONString(operationArgs));
                taskInfoQueryWrapper.orderByDesc(TaskInfo::getCreateDate);
                List<TaskInfo> taskInfos = taskService.list(taskInfoQueryWrapper);
                TaskInfo taskInfo = null;
                if (taskInfos.size() > 0) {
                    taskInfo = taskInfos.get(0);
                }
                if (taskInfo != null) {
                    Map msgData = JSON.parseObject(taskInfo.getTaskMsg(), Map.class);
                    com.alibaba.fastjson.JSONObject rawData = (com.alibaba.fastjson.JSONObject) msgData.get("data");
                    status.put("start_date", rawData.get("start_date"));
                    status.put("end_date", rawData.get("end_date"));
                }

            }
            status.put("status", data.getContainerStatus());
            status.put("is_check", data.getIScheck());
            status.put("container_id", data.getContainerId());
//            status.put("task_id", taskInfo.getTaskId().toString());
//            status.put("progress_status", "share");
        }
        status.put("now", Instant.now().getEpochSecond());
        imageDTO.setStatus(status);
        return imageDTO;
    }


    /*
    靶场首页信息获取接口返回值信息
    count:1
next: null
previous: null
result:{

create_date: "2023-04-01T18:42:13.511164"
image_desc: "vulfocus/php-fpm-fastcgi"
image_id: "23f6aad6-9164-421d-9f68-5c3dbe777cf7"
image_name:
"vulfocus/php-fpm-fastcgi:latest"
image_port: ""
image_vul_name: "vulfocus/php-fpm-fastcgi"
is_ok: true
is_share: false
rank: 3.5
update_date: "2023-04-01T18:42:48.383246"

status: {
container_id: ""
end_date: ""
host: ""
is_check: false
now: 1680489609
port: ""
progress: 0
progress_status: ""
start_date: ""
status: ""
task_id: ""
}

}

     */


    private Result handlerPage(Page<ImageInfo> page, UserDTO user) throws Exception {
        long timeNow = Utils.dataTimeToTimestamp(LocalDateTime.now());
        TimeMoudel data = timeMoudelService.query().eq("user_id", user.getId()).ge("end_time", timeNow).one();
        HashMap<String, Object> map = new HashMap<String, Object>();
        List<ImageDTO> imageDTOS = new ArrayList<>();
        List<ImageInfo> result = page.getRecords();
        for (ImageInfo imageInfo : result) {
            ImageDTO imageDTO = handlerImageDTO(imageInfo, user);
            imageDTOS.add(imageDTO);
        }
        if(data!=null){
            for (ImageDTO imageDTO : imageDTOS) {
                imageDTO.setImageDesc("");
                imageDTO.setImageName("");
                imageDTO.setImageVulName("");
            }
        }
        Page<ImageDTO> imageDTOPage = new Page<>();
        BeanUtils.copyProperties(page, imageDTOPage);
        imageDTOPage.setRecords(imageDTOS);
        return Result.ok(imageDTOPage);
    }


    private String getImageId(String id) {
        return id.split(":")[1];
    }

}

package com.sunzy.vulfocus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.dockerjava.api.model.Image;
import com.sunzy.vulfocus.common.ErrorClass;
import com.sunzy.vulfocus.common.Result;
import com.sunzy.vulfocus.common.SystemConstants;
import com.sunzy.vulfocus.model.dto.ImageDTO;
import com.sunzy.vulfocus.model.dto.UserDTO;
import com.sunzy.vulfocus.model.po.ContainerVul;
import com.sunzy.vulfocus.model.po.ImageInfo;
import com.sunzy.vulfocus.mapper.ImageInfoMapper;
import com.sunzy.vulfocus.model.po.LocalImage;
import com.sunzy.vulfocus.model.po.UserUserprofile;
import com.sunzy.vulfocus.service.ContainerVulService;
import com.sunzy.vulfocus.service.ImageInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunzy.vulfocus.service.UserUserprofileService;
import com.sunzy.vulfocus.utils.DockerTools;
import com.sunzy.vulfocus.utils.UserHolder;
import org.apache.catalina.User;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import sun.plugin.util.UserProfile;

import javax.annotation.Resource;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author sunzy
 * @since 2023-04-01
 */
@Service
public class ImageInfoServiceImpl extends ServiceImpl<ImageInfoMapper, ImageInfo> implements ImageInfoService {

    @Resource
    private ImageInfoMapper imageInfoMapper;

    @Resource
    private UserUserprofileService userService;

    @Resource
    private ContainerVulService containerService;


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
        wrapper.eq(ImageInfo::getOk, "0");
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
//        Long userId = userDTO.getId();
//        UserUserprofile user = userService.getById(userId);
        Page<ImageInfo> imageInfoPage = new Page<>(page, SystemConstants.PAGE_SIZE);
        LambdaQueryWrapper<ImageInfo> wrapper = new LambdaQueryWrapper<>();
//        if(user.getSuperuser()){
        if (true) {
            if (!"".equals(query)) {
                query = query.trim();
                if (!"".equals(flag) && "flag".equals(flag)) {
                    wrapper.like(!"".equals(query), ImageInfo::getImageName, query);
                    wrapper.like(!"".equals(query), ImageInfo::getImageDesc, query);
                    wrapper.like(!"".equals(query), ImageInfo::getImageVulName, query);
                    wrapper.orderBy(true, false, ImageInfo::getCreateDate);
                    page(imageInfoPage, wrapper);
                    return hanlderPage(imageInfoPage, user);
                } else {
                    wrapper.like(!"".equals(query), ImageInfo::getImageName, query);
                    wrapper.like(!"".equals(query), ImageInfo::getImageDesc, query);
                    wrapper.like(!"".equals(query), ImageInfo::getImageVulName, query);
                    wrapper.eq(true, ImageInfo::getOk, true);
                    wrapper.orderBy(true, false, ImageInfo::getCreateDate);
                    page(imageInfoPage, wrapper);
                    return hanlderPage(imageInfoPage, user);
                }
            } else {
                if (!"".equals(flag) && "flag".equals(flag)) {
                    wrapper.eq(true, ImageInfo::getOk, true);
                    page(imageInfoPage, wrapper);
                    return hanlderPage(imageInfoPage, user);
                } else {
                    wrapper.eq(true, ImageInfo::getOk, true);
                    wrapper.orderBy(true, false, ImageInfo::getCreateDate);
                    page(imageInfoPage, wrapper);
                    return hanlderPage(imageInfoPage, user);
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
                return hanlderPage(imageInfoPage, user);
            } else {
                wrapper.eq(true, ImageInfo::getOk, true);
                wrapper.orderBy(true, false, ImageInfo::getCreateDate);
                page(imageInfoPage, wrapper);
                return hanlderPage(imageInfoPage, user);
            }
        }
    }


    private ImageDTO handleImageDTO(ImageInfo imageInfo, UserDTO user) throws Exception {
        ImageDTO imageDTO = new ImageDTO();
        BeanUtils.copyProperties(imageInfo, imageDTO);
//        Integer userId = null;
//        if (user != null) {
//            userId = user.getId();
//        }
        Integer userId = 1;
        Map<String, Object> status = new HashMap<>();
        // 查询该用户创建的容器
        LambdaQueryWrapper<ContainerVul> wraper = new LambdaQueryWrapper<>();
        wraper.eq(userId != null, ContainerVul::getUserId, userId);
        wraper.eq(imageInfo != null, ContainerVul::getImageIdId, imageInfo.getImageId());
        ContainerVul data = null;
        try {
            data = containerService.getOne(wraper);
        } catch (Exception e){
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
//                TaskInfo taskInfo = TaskInfoRepository.findFirstByUserIdAndTaskStatusAndOperationTypeAndOperationArgsOrderByCreateDateDesc(
//                        id, 3, 2, JsonUtils.toJson(operationArgs));
//                if (taskInfo != null) {
//                    try {
//                        Map<String, Object> taskMsg = JsonUtils.toMap(taskInfo.getTaskMsg());
//                        status.put("start_date", taskMsg.get("data.start_date"));
//                        status.put("end_date", taskMsg.get("data.end_date"));
//                    } catch (Exception e) {
//                        status.put("start_date", "");
//                        status.put("end_date", "");
//                    }
//                }
            }
            status.put("status", data.getContainerStatus());
            status.put("is_check", data.getIScheck());
            status.put("container_id", data.getContainerId());
//            status.put("task_id", taskInfo.getTaskId().toString());
            status.put("progress_status", "share");
//            try {
//                String taskLog = RedisUtil.get(taskInfo.getTaskId().toString());
//                Map<String, Object> taskLogJson = JsonUtils.toMap(taskLog);
//                status.put("progress", taskLogJson.get("progress"));
//            } catch (Exception e) {
//            }
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


    private Result hanlderPage(Page<ImageInfo> page, UserDTO user) throws Exception {
        HashMap<String, Object> map = new HashMap<String, Object>();
        List<ImageDTO> imageDTOS = new ArrayList<>();
        List<ImageInfo> result = page.getRecords();
        for (ImageInfo imageInfo : result) {
            ImageDTO imageDTO = handleImageDTO(imageInfo, user);
            imageDTOS.add(imageDTO);
        }
        Page<ImageDTO> imageDTOPage = new Page<>();
        BeanUtils.copyProperties(page, imageDTOPage);
        imageDTOPage.setRecords(imageDTOS);
        return Result.ok(imageDTOPage);
    }

    @Override
    public boolean importImage() {
        // 1.从docker服务器中获取images,讲镜像信息导入数据库中
        List<Image> images = DockerTools.imageList();
        /**
         *
         *     imageinfo
         *      private String imageId;
         *
         *     private String imageName;
         *
         *     private String imageVulName;
         *
         *     private String imagePort;
         *
         *     private String imageDesc;
         *
         *     private Double rank;
         *
         *     @TableField("is_ok")
         *     private Boolean ok;
         *
         *     private LocalDateTime createDate;
         *
         *     private LocalDateTime updateDate;
         *
         *     @TableField("is_share")
         *     private Boolean share;
         *
         *     private String degree;
         *
         *     private String isStatus;
         */
// Image(created=1680113964, id=sha256:9ed4aefc74f6792b5a804d1d146fe4b4a2299147b0f50eaf2b08435d7b38c27e,
// parentId=, repoTags=[alpine:latest], repoDigests=[alpine@sha256:124c7d2707904eea7431fffe91522a01e5a861a624ee31d03372cc1d138a3126],
// size=7049701, virtualSize=7049701, sharedSize=-1, labels=null, containers=-1)
        List<ImageInfo> imageInfoList = new ArrayList<ImageInfo>();
        for (Image image : images) {

        }

        try {

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        return false;
    }


    private ImageInfo imageToImageInfo(Image image) {
        String id = getImageId(image.getId());
        String name = getImageName(image.getRepoTags());
        ImageInfo imageInfo = new ImageInfo();
        imageInfo.setImageId(id);
        imageInfo.setImageName(name);
        imageInfo.setImageVulName(name);

        imageInfo.setImagePort("80");
        imageInfo.setImageDesc(name);
        imageInfo.setDegree("1");
        imageInfo.setIsStatus("1");
        imageInfo.setOk(true);
        imageInfo.setShare(false);
        return imageInfo;
    }

    private String getImageId(String id) {
        return id.split(":")[1];
    }

    private String getImageName(String[] repoTags) {
        return repoTags[0];
    }
}

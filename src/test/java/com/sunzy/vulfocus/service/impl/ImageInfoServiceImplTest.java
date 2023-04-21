package com.sunzy.vulfocus.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sunzy.vulfocus.common.Result;
import com.sunzy.vulfocus.model.dto.CreateImage;
import com.sunzy.vulfocus.model.dto.UserDTO;
import com.sunzy.vulfocus.model.po.ImageInfo;
import com.sunzy.vulfocus.model.po.LocalImage;
import com.sunzy.vulfocus.service.ImageInfoService;
import com.sunzy.vulfocus.common.SystemConstants;
import com.sunzy.vulfocus.service.TaskInfoService;
import com.sunzy.vulfocus.utils.Utils;
import com.sunzy.vulfocus.utils.UserHolder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@SpringBootTest
class ImageInfoServiceImplTest {
    @Resource
    private ImageInfoService imageInfoService;

    @Resource
    private TaskInfoService taskService;
    @Test
    void getLocalImages() {
        Result res = imageInfoService.getLocalImages();
        List<LocalImage> data = (ArrayList<LocalImage>) res.getData();
        System.out.println(data);
    }

    @Test
    void testPageQuery() {
        Page<ImageInfo> page = new Page<>(1, SystemConstants.PAGE_SIZE);
        Page<ImageInfo> page1 = imageInfoService.page(page);

    }

    @Test
    void testStartContainer() throws InterruptedException {
        UserDTO user = new UserDTO();
        user.setSuperuser(true);
        user.setId(1);
        UserHolder.saveUser(user);
        imageInfoService.startContainer("4fc16ba5c9c149dd96f3f9d52d544f53");
        Thread.sleep(10000);
    }


    @Test
    void testGetLocalImages(){
        Result localImages = imageInfoService.getLocalImages();
        Object data = localImages.getData();
        System.out.println(data);
    }

    @Test
    void testBatchLocal(){
        UserDTO user = new UserDTO();
        user.setSuperuser(true);
        user.setId(1);
        UserHolder.saveUser(user);
        imageInfoService.batchLocalAdd("vulfocus/struts2-cve_2017_9791:latest");

//        batchLocalAdd("vulfocus/struts2-cve_2017_9791:latest");
    }


    private Result batchLocalAdd(String imageNamesStr) {
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
            ImageInfo imageInfo = imageInfoService.query().eq("image_name", imageName).one();
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
                imageInfoService.save(imageInfo);
            }

            String taskId = taskService.createImageTask(imageInfo, user, null);
            if(!StrUtil.isBlank(taskId)){
                resp.add("拉取镜像" + imageName + "任务下发成功");
            }
        }
        return Result.ok(resp);
    }




    @Test
    void testCreateImage() throws Exception {
        UserDTO userDTO = new UserDTO();
        userDTO.setSuperuser(true);
        userDTO.setId(1);
        UserHolder.saveUser(userDTO);
        CreateImage image = new CreateImage();
        image.setImageName("redis:latest");
        image.setImageVulName("redis:latest");
        image.setImageDesc("redis");
        image.setRank(2.5);
        imageInfoService.createImage(image);
        Thread.sleep(6000 * 100);
    }
}
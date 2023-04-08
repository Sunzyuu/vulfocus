package com.sunzy.vulfocus.service.impl;


import com.sunzy.vulfocus.model.dto.UserDTO;
import com.sunzy.vulfocus.model.po.ContainerVul;
import com.sunzy.vulfocus.model.po.ImageInfo;
import com.sunzy.vulfocus.service.ContainerVulService;
import com.sunzy.vulfocus.service.TaskInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class TaskInfoServiceImplTest {

    @Resource
    private TaskInfoServiceImpl taskService;

    @Resource
    private ContainerVulService containerService;


    @Test
    public void createImageTask() throws Exception {
        ImageInfo imageInfo = new ImageInfo();
        UserDTO user = new UserDTO();
        imageInfo.setImageName("vulfocus/php-fpm-fastcgi:latest");
        imageInfo.setImageVulName("vulfocus/php-fpm-fastcgi");
        imageInfo.setImageDesc("vulfocus/php-fpm-fastcgi");
        imageInfo.setImageDesc("vulfocus/php-fpm-fastcgi");
        imageInfo.setRank(2.5);

        user.setId(1);
        user.setRequestIp("127.0.0.1");
        user.setSuperuser(true);
        taskService.createImageTask(imageInfo, user);
    }

    @Test
    void testTaskInfo() throws Exception {
        UserDTO user = new UserDTO();
        user.setId(1);
        ImageInfo imageInfo = new ImageInfo();
        imageInfo.setImageName("vulfocus/vulfocus");
        taskService.createCreateImageTask(imageInfo, user);
    }


    @Test
    void testCreateContainer() throws Exception {
        UserDTO user = new UserDTO();
        user.setId(1);
        user.setSuperuser(true);
        user.setRequestIp("127.0.0.1");
        ContainerVul containerVul = containerService.query().eq("container_id", "daf984b7d92547388d76f6cb5fbf0299").one();
        taskService.createContainerTask(containerVul, user);
    }
}
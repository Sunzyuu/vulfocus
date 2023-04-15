package com.sunzy.vulfocus.service.impl;


import com.sunzy.vulfocus.model.dto.UserDTO;
import com.sunzy.vulfocus.model.po.ContainerVul;
import com.sunzy.vulfocus.model.po.ImageInfo;
import com.sunzy.vulfocus.service.ContainerVulService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.scheduling.annotation.EnableAsync;

import javax.annotation.Resource;

@Slf4j
@SpringBootTest
@EnableAsync
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
        taskService.createImageTask(imageInfo, user, null);
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
        ContainerVul containerVul = containerService.query().eq("container_id", "baa18de88a37454bb597546a7dda44ff").one();
        taskService.createContainerTask(containerVul, user);
    }

    @Test
    void testStopContainerTask() throws Exception {
        UserDTO user = new UserDTO();
        user.setId(1);
        user.setSuperuser(true);
        user.setRequestIp("127.0.0.1");
        ContainerVul containerVul = containerService.query().eq("container_id", "baa18de88a37454bb597546a7dda44ff").one();
        taskService.stopContainerTask(containerVul, user);
        Thread.sleep(5000);

    }

    @Test
    void getBatchTask() {
        String ids = "84b6479806014da1bb5e9caf359773f6,39648935112443218cebc3e45c68baf1,6f7e8ee27f8d4d28a70bd2b74c760abc";
        taskService.getBatchTask(ids);
    }

    @Test
    void testDeleteContainerTask() throws Exception {
        UserDTO user = new UserDTO();
        user.setId(1);
        user.setSuperuser(true);
        user.setRequestIp("127.0.0.1");
        ContainerVul containerVul = containerService.query().eq("container_id", "daf984b7d92547388d76f6cb5fbf0299").one();
        taskService.deleteContainerTask(containerVul, user);

    }

    @Test
    void testAsync(){
        log.info("==============开始");
        taskService.getStatus();
        log.info("==============结束");

    }


}
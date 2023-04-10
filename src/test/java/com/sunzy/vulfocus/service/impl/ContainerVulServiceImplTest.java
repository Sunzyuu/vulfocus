package com.sunzy.vulfocus.service.impl;

import com.sunzy.vulfocus.model.dto.UserDTO;
import com.sunzy.vulfocus.service.ContainerVulService;
import com.sunzy.vulfocus.utils.UserHolder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ContainerVulServiceImplTest {

    @Resource
    private ContainerVulService containerService;
    @Test
    public void testCheckFlag(){
        UserDTO user = new UserDTO();
        user.setSuperuser(true);
        user.setName("sunzy");
        user.setId(1);
        UserHolder.saveUser(user);
        containerService.checkFlag("flag{ad116b24-18fb-4725-81f5-a292c5e77005}", "daf984b7d92547388d76f6cb5fbf0299");
    }

    @Test
    void testStartContainer() {
        UserDTO user = new UserDTO();
        user.setSuperuser(true);
        user.setName("sunzy");
        user.setId(1);
        UserHolder.saveUser(user);

        containerService.startContainer("daf984b7d92547388d76f6cb5fbf0299");
    }

    @Test
    void testStopContainer() {
        UserDTO user = new UserDTO();
        user.setSuperuser(true);
        user.setName("sunzy");
        user.setId(1);
        UserHolder.saveUser(user);

        containerService.stopContainer("daf984b7d92547388d76f6cb5fbf0299");
    }

    @Test
    void deleteStopContainer() {
        UserDTO user = new UserDTO();
        user.setSuperuser(true);
        user.setName("sunzy");
        user.setId(1);
        UserHolder.saveUser(user);

        containerService.deleteContainer("daf984b7d92547388d76f6cb5fbf0299");
    }
}
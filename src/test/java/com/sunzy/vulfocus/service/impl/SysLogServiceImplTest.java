package com.sunzy.vulfocus.service.impl;


import com.sunzy.vulfocus.common.Result;
import com.sunzy.vulfocus.model.dto.UserDTO;
import com.sunzy.vulfocus.service.SysLogService;
import com.sunzy.vulfocus.utils.UserHolder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class SysLogServiceImplTest {

    @Resource
    private SysLogService logService;


    @Test
    void testGetLogservice() {
        UserDTO user = new UserDTO();
        user.setSuperuser(true);
        UserHolder.saveUser(user);
        Result res = logService.getSysLog(1, "镜像");
        Object data = res.getData();
        System.out.println(data);
    }
}
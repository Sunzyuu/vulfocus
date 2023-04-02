package com.sunzy.vulfocus.service.impl;

import com.sunzy.vulfocus.common.Result;
import com.sunzy.vulfocus.model.dto.UserDTO;
import com.sunzy.vulfocus.service.UserUserprofileService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;


@SpringBootTest
class UserUserprofileServiceImplTest {

    @Resource
    private UserUserprofileService userService;


    @Test
    public void testRegister(){

        UserDTO userDto = new UserDTO();
        userDto.setName("sunzy1");
        userDto.setPass("111111");
        userDto.setCheckPass("111111");
        userDto.setEmail("111@qq.com");
        Result result = userService.register(userDto);
        System.out.println(result.getMsg());
    }

    @Test
    public void testLogin(){
        UserDTO userDto = new UserDTO();
        userDto.setName("sunzy1");
        userDto.setPass("111111");

        Result login = userService.login(userDto);
        System.out.println(login.getData());
    }
}
package com.sunzy.vulfocus.service.impl;


import com.sunzy.vulfocus.common.Result;
import com.sunzy.vulfocus.model.dto.NetworkDTO;
import com.sunzy.vulfocus.model.dto.UserDTO;
import com.sunzy.vulfocus.service.NetWorkInfoService;
import com.sunzy.vulfocus.utils.UserHolder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
class NetWorkInfoServiceImplTest {

    @Resource
    private NetWorkInfoService netWorkInfoService;
    @Test
    void testCreateNetWorkInfo() {
        UserDTO user = new UserDTO();
        user.setSuperuser(true);
        user.setId(1);

        UserHolder.saveUser(user);
        NetworkDTO networkDTO = new NetworkDTO();
        networkDTO.setNetWorkName("demo1");
        networkDTO.setNetWorkSubnet("192.168.3.0/24");
        networkDTO.setNetWorkGateway("192.168.3.1");
        networkDTO.setNetWorkScope("local");
        networkDTO.setNetWorkDriver("bridge");
        networkDTO.setEnableIpv6(false);

        Result netWorkInfo = netWorkInfoService.createNetWorkInfo(networkDTO);
        System.out.println(netWorkInfo);

    }

    @Test
    void testRemoveNetWork() {
        UserDTO user = new UserDTO();
        user.setSuperuser(true);
        user.setId(1);
        UserHolder.saveUser(user);
        netWorkInfoService.removeNetWorkInfo("d13f43aa216b446cbc92f64c7f995225");
    }

    @Test
    void testGetNetWorkInfoList() {
        UserDTO user = new UserDTO();
        user.setSuperuser(true);
        user.setId(1);

        UserHolder.saveUser(user);
        Result demo = netWorkInfoService.getNetWorkInfoList("");
        System.out.println(demo.getData());
    }
}
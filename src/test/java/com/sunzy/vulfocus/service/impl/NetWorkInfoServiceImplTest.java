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
        networkDTO.setNetWorkName("demo");
        networkDTO.setNetWorkSubnet("192.168.1.1/24");
        networkDTO.setNetWorkGateway("192.168.1.1");
        networkDTO.setNetWorkScope("local");
        networkDTO.setNetWorkDriver("bridge");
        networkDTO.setEnableIpv6(false);

        Result netWorkInfo = netWorkInfoService.createNetWorkInfo(networkDTO);
        System.out.println(netWorkInfo);

    }
}
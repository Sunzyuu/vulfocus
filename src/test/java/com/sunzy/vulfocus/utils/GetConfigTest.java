package com.sunzy.vulfocus.utils;


import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest
class GetConfigTest {


    @Test
    void getConfig() throws Exception {
        Map<String, Object> map = GetConfig.get();
        System.out.println(map);
    }

    @Test
    void testGetBaseDir() throws Exception {
        String property = System.getProperty("user.dir");
        System.out.println(property);
    }
}
package com.sunzy.vulfocus.utils;

import org.springframework.beans.factory.annotation.Value;

import java.util.HashMap;
import java.util.Map;

public class GetConfig {
    private static String shareName = "vulfocus";

    private static String username = "vulfocus";

    private static String pwd = "123456";

    private static int time = 30 * 60;

    public static Map<String, Object> get(){
        Map<String, Object> config = new HashMap<>();
        config.put("share_name", shareName);
        config.put("username", username);
        config.put("pwd", pwd);
        config.put("time", time);
        return config;
    }
}

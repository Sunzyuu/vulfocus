package com.sunzy.vulfocus.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.HexUtil;
import cn.hutool.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class test {
    public static void main(String[] args) {
//        String baseTargetPort = "11111";
//        String encodeBaseTargetPort = Base64.encode(baseTargetPort);
//        System.out.println(encodeBaseTargetPort);
//        char[] str = HexUtil.encodeHex(encodeBaseTargetPort, StandardCharsets.UTF_8);
//        System.out.println(new String(str));
        ArrayList<String> portList = new ArrayList<>();
        portList.add("80");
        portList.add("8080");
        System.out.println(portList.toString());
    }
}

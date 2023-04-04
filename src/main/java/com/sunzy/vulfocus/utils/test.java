package com.sunzy.vulfocus.utils;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.Map;

public class test {

    public static void main(String[] args) {


        Map<String, String> map = new HashMap<>();
        map.put("image_name", "1111");

        String string = JSON.toJSONString(map);
        System.out.println(string);

        Map map1 = JSON.parseObject(string, Map.class);

        Object name = map1.get("image_name");
        System.out.println((String) name);
        String ss = "{\"image_name\":\"vulfocus/thinkphp5:latest\"}";
        JSONObject jsonObject = JSONUtil.parseObj(ss);
        System.out.println(jsonObject.toString());
    }
}

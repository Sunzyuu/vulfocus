package com.sunzy.vulfocus.utils;

import cn.hutool.core.util.IdUtil;
import org.yaml.snakeyaml.Yaml;

import java.util.Map;

public class Utils {

    public static String getUUID(){
        return IdUtil.simpleUUID();
    }

    public static String jsonToYaml(String jsonStr) {
        Yaml yaml = new Yaml();
        Map<String,Object> map = (Map<String, Object>) yaml.load(jsonStr);
        return yaml.dumpAsMap(map);
    }
}

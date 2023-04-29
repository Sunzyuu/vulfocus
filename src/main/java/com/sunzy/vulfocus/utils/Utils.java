package com.sunzy.vulfocus.utils;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.MD5;
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

    public static String md5(String data){
        MD5 md5 = MD5.create();
        return md5.digestHex(data);
    }
}

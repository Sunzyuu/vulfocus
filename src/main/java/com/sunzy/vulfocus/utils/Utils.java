package com.sunzy.vulfocus.utils;

import cn.hutool.core.util.IdUtil;
import cn.hutool.crypto.digest.MD5;
import org.yaml.snakeyaml.Yaml;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
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


    public static LocalDateTime timeStampToDatetime(long timestamp){
        Instant instant = Instant.ofEpochMilli(timestamp);
        return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
    }

    public static long dataTimeToTimestamp(LocalDateTime ldt){
        return ldt.toInstant(ZoneOffset.of("+8")).toEpochMilli();
    }
}

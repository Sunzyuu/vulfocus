package com.sunzy.vulfocus.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.HexUtil;
import cn.hutool.json.JSONObject;
import org.yaml.snakeyaml.Yaml;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class test {
    public static void main(String[] args) {
//        String baseTargetPort = "11111";
//        String encodeBaseTargetPort = Base64.encode(baseTargetPort);
//        System.out.println(encodeBaseTargetPort);
//        char[] str = HexUtil.encodeHex(encodeBaseTargetPort, StandardCharsets.UTF_8);
//        System.out.println(new String(str));
//        ArrayList<String> portList = new ArrayList<>();
//        portList.add("80");
//        portList.add("8080");
//        System.out.println(portList.toString());

        Yaml yaml = new Yaml();
//将 JSON 字符串转成 Map
//        String jsonStr = "{\"nodes\":[{\"name\":\"tesA\",\"type\":\"command\",\"config\":{\"command\":\"echo \\\"tesA\\\"\"}},{\"name\":\"testB\",\"type\":\"command\",\"config\":{\"command\":\"echo \\\"tesB\\\"\"},\"dependsOn\":[\"tesA\"]},{\"name\":\"testC\",\"type\":\"command\",\"config\":{\"command\":\"echo \\\"tesC\\\"\"},\"dependsOn\":[\"testB\"]}]}";
        String jsonStr = "{\"services\":{\"63yorm3qmro0\":{\"image\":\"redis:3.2-alpine\",\"ports\":[\"${VULFOCUS4e6a4e3562334a744d334674636d38774c54597a4e7a6b3d}:6379\"],\"networks\":[\"network-002\"]},\"gargmjrhv2o\":{\"image\":\"redis:3.2-alpine\",\"networks\":[\"network-002\"]}},\"networks\":{\"network-002\":{\"external\":true}},\"version\":\"3.2\"}";
//        jsonStr = "{'version': '3.2', 'services': {'63yorm3qmro0': {'image': 'redis:3.2-alpine', 'ports': ['${VULFOCUS4E6A4E3562334A744D334674636D38774C54597A4E7A6B3D}:6379'], 'networks': ['network-002']}, 'gargmjrhv2o': {'image': 'redis:3.2-alpine', 'networks': ['network-002']}}, 'networks': {'network-002': {'external': true}}}";
//        Map<String,Object> map = (Map<String, Object>) yaml.load(jsonStr);
//转换成 YAML 字符串
//        String yamlStr = yaml.dumpAsMap(map);
//        System.out.println(yamlStr);

        JSONObject services = new JSONObject(jsonStr);

        JSONObject services1 = (JSONObject)services.get("services");
        Set<Map.Entry<String, Object>> entries = services1.entrySet();
        for (Map.Entry<String, Object> entry : entries) {
            System.out.println(entry.getKey());
        }
    }

    /*
networks:
  network-002:
    external: true
services:
  63yorm3qmro0:
    image: redis:3.2-alpine
    networks:
    - network-002
    ports:
    - ${VULFOCUS4E6A4E3562334A744D334674636D38774C54597A4E7A6B3D}:6379
  gargmjrhv2o:
    image: redis:3.2-alpine
    networks:
    - network-002
version: '3.2'


services:
  63yorm3qmro0:
    image: redis:3.2-alpine
    ports:
    - ${VULFOCUS4e6a4e3562334a744d334674636d38774c54597a4e7a6b3d}:6379
    networks:
    - network-002
  gargmjrhv2o:
    image: redis:3.2-alpine
    networks:
    - network-002
networks:
  network-002:
    external: true
version: '3.2'

services:
  63yorm3qmro0:
    image: redis:3.2-alpine
    ports: ['${VULFOCUS4e6a4e3562334a744d334674636d38774c54597a4e7a6b3d}:6379']
    networks: [network-002]
  gargmjrhv2o:
    image: redis:3.2-alpine
    networks: [network-002]
networks:
  network-002: {external: true}
version: '3.2'


version: '3.2'
services:
  63yorm3qmro0:
    image: redis:3.2-alpine
    ports: ['${VULFOCUS4E6A4E3562334A744D334674636D38774C54597A4E7A6B3D}:6379']
    networks: [network-002]
  gargmjrhv2o:
    image: redis:3.2-alpine
    networks: [network-002]
networks:
  network-002: {external: true}

     */
}

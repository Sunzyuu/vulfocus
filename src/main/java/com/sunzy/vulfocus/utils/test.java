package com.sunzy.vulfocus.utils;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;
import cn.hutool.json.JSONObject;
import com.github.dockerjava.api.model.Container;
import com.sunzy.vulfocus.common.SystemConstants;
import org.yaml.snakeyaml.Yaml;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static com.sunzy.vulfocus.common.ErrorClass.PortInvalidException;

public class test {
    public static void main(String[] args) throws IOException {
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

//        JSONObject services = new JSONObject(jsonStr);
//
//        JSONObject services1 = (JSONObject)services.get("services");
//        Set<Map.Entry<String, Object>> entries = services1.entrySet();
//        for (Map.Entry<String, Object> entry : entries) {
//            System.out.println(entry.getKey());
//        }

//        String testString = "VULFOCUS4e6a4e3562334a744d334674636d38774c54597a4e7a6b3d\n";
//        String[] split = testString.split("\n");
//        System.out.println(split.length);
//        Process process = Runtime.getRuntime()
//                .exec("docker-compose up -d", null, new File("E:\\vulfocus-0.3.2.3\\vulfocus-0.3.2.2\\vulfocus-api\\docker-compose\\56d186f2-d64c-48a1-8a21-f6630bd707ef"));
//        printResults(process);
//        System.out.println("finished");
//        DockerTools.dockerComposeUp(new File("E:\\vulfocus-0.3.2.3\\vulfocus-0.3.2.2\\vulfocus-api\\docker-compose\\56d186f2-d64c-48a1-8a21-f6630bd707ef"), "docker-compose up -d");

//        String test = "56d186f2-d64c-48a1-8a21-f6630bd707ef-7h4vhg3c58w0-1   redis:latest        \"docker-entrypoint.s…\"   7h4vhg3c58w0        2 hours ago         Up 5 minutes        0.0.0.0:25138->6379/tcp\n";
//        String[] split = test.split(" ");
//        System.out.println(split[0]);

//        ArrayList<String> name = new ArrayList<>();
//
//        name.add("56d186f2-d64c-48a1-8a21-f6630bd707ef-7h4vhg3c58w0-1");
//        name.add("56d186f2-d64c-48a1-8a21-f6630bd707ef-t3a2i35fqv4-1");
//        ArrayList<Container> containersByName = DockerTools.getContainersByName(name);
//        System.out.println(containersByName);


//        HashMap<String, Double> map = new HashMap<>();
//        map.put("1", 2.5);
//        map.put("5", 2.5);
//        map.put("2", 1.5);
//        map.put("4", 6.5);
//        map.put("7", 3.5);
//        List<Double> list = map.entrySet().stream()
////                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder())) //降序
//                .sorted(Map.Entry.comparingByValue()) //升序
//                .map(Map.Entry::getValue)
//                .collect(Collectors.toList());
//        System.out.println(list.toString());
//
//        System.out.println(map);
//        extracted();
        MD5 md5 = MD5.create();
        String md5Token = md5.digestHex("111");
        System.out.println(md5Token);

    }

    private static void extracted() {
        Map<String, Integer> codes = new HashMap<>();
        codes.put("United States", 1);
        codes.put("Germany", 49);
        codes.put("France", 33);
        codes.put("China", 86);
        codes.put("Pakistan", 92);

        extracted(codes);
    }

    private static void extracted(Map<String, Integer> codes) {
        // 按照Map的键进行排序
        Map<String, Integer> sortedMap = codes.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .collect(
                        Collectors.toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (oldVal, newVal) -> oldVal,
                                LinkedHashMap::new
                        )
                );
// 将排序后的Map打印
        sortedMap.entrySet().forEach(System.out::println);
    }


    public static void printResults(Process process) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = "";
        while ((line = reader.readLine()) != null) {
            System.out.println(line);
        }
    }


//    private String getRandomPort(String envContent) throws Exception {
//        ArrayList<String> randomList = new ArrayList<>();
//        StringBuilder resultPortList = new StringBuilder();
//        String[] envs = envContent.split("\n");
//        for (String port : envs) {
//            if(StrUtil.isBlank(port)){
//                continue;
//            }
//            String randomPort = "";
//            for (int i = 0; i < 20; i++) {
//                randomPort = DockerTools.getRandomPort();
//                if (randomList.contains(randomPort) || containerService.query().eq("container_port", randomPort).one() != null) {
//                    continue;
//                }
//                break;
//            }
//            if (StrUtil.isBlank(randomPort)) {
//                throw PortInvalidException;
//            }
//            randomList.add(randomPort);
//            resultPortList.append(port).append("=").append(randomPort).append("\n");
//        }
//        return resultPortList.toString();
//    }

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

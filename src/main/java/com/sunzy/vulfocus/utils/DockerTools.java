package com.sunzy.vulfocus.utils;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSONArray;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.github.dockerjava.okhttp.OkDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import org.apache.commons.lang.StringUtils;
import org.bouncycastle.crypto.prng.RandomGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import sun.security.provider.SecureRandom;

import java.io.ByteArrayOutputStream;
import java.util.*;

@Component
public class DockerTools {

    private static DockerClient dockerClient = null;

//    private static RandomGenerator random = new SecureRandom();

    public static DockerClient getDockerClient() {
        DockerClientConfig dockerClientConfig = DefaultDockerClientConfig
                .createDefaultConfigBuilder()
                .build();
        DockerHttpClient httpClient = new OkDockerHttpClient.Builder()
                .dockerHost(dockerClientConfig.getDockerHost())
                .sslConfig(dockerClientConfig.getSSLConfig())
                .build();
        dockerClient = DockerClientImpl.getInstance(dockerClientConfig, httpClient);
        return dockerClient;
    }

    @Bean
    public static void connectDocker() {
        DockerClientConfig dockerClientConfig = DefaultDockerClientConfig
                .createDefaultConfigBuilder()
                .build();
        DockerHttpClient httpClient = new OkDockerHttpClient.Builder()
                .dockerHost(dockerClientConfig.getDockerHost())
                .sslConfig(dockerClientConfig.getSSLConfig())
                .build();
        dockerClient = DockerClientImpl.getInstance(dockerClientConfig, httpClient);
    }

    public static Info queryClientInfo(DockerClient dockerClient) {
        return dockerClient.infoCmd().exec();
    }


    public static String createContainer(String imageName, String containerName, HostConfig hostConfig, List<ExposedPort> portList, List<String> cmd){
        CreateContainerResponse container = dockerClient.createContainerCmd(imageName)
                .withName(containerName)
                .withHostConfig(hostConfig)
                .withExposedPorts(portList)
                .withCmd(cmd)
                .exec();
        return container.getId();
    }

    public static String runContainerWithPorts(String imageName, Map<String, Integer> ports){
        Set<Map.Entry<String, Integer>> entries = ports.entrySet();
        Ports portBindings = new Ports();
        List<ExposedPort> exposedPortList = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : entries) {
            String port = entry.getKey();
            portBindings.bind(new ExposedPort(Integer.parseInt(port)), Ports.Binding.bindPort(entry.getValue()));
            exposedPortList.add(new ExposedPort(Integer.parseInt(port)));
        }
        String id = dockerClient.createContainerCmd(imageName)
                .withPortBindings(portBindings)
                .withExposedPorts(exposedPortList)
                .exec()
                .getId();
        dockerClient.startContainerCmd(id).exec();
        return id;
    }

    public static Object startContainerCmd(String containerID) {
        return dockerClient.startContainerCmd(containerID).exec();
    }

    public static Object restartContainerCmd(String containerID) {
        return dockerClient.restartContainerCmd(containerID).exec();
    }

    public static boolean stopContainer(String container) {
        dockerClient.stopContainerCmd(container).exec();
        return true;
    }

    public static Container getContainerById(String containerID) {
        List<Container> containers = dockerClient.listContainersCmd().withShowAll(true).withIdFilter(Collections.singleton(containerID)).exec();
        if(containers.size() == 0){
            return null;
        }
        return containers.get(0);
    }

    public static List<Image> imageList(){
        List<Image> imageList = dockerClient.listImagesCmd().exec();
        return imageList;
    }

    /**
     * 删除镜像
     *
     * @param imagesID     镜像ID
     * @return Object
     */
    public static Object removeImages(String imagesID) {
        Void exec = dockerClient.removeImageCmd(imagesID).exec();
        return dockerClient.removeImageCmd(imagesID).exec();
    }

    public static InspectImageResponse getImageByName(String name) {
        List<Image> imageList = dockerClient.listImagesCmd().exec();
        for (Image image : imageList) {
            System.out.println(Arrays.toString(image.getRepoTags()));
            if(name.equals(image.getRepoTags()[0])){
                return dockerClient.inspectImageCmd(name).exec();
            }
        }
        return null;
    }


    public static List<Container> containerList(){
        return dockerClient.listContainersCmd().withShowAll(true).exec();
    }

    public static List<String> getContainerNameList(List<Container> containerList) {
        List<String> containerNameList = new ArrayList<>();
        for (Container container : containerList) {
            String containerName = container.getNames()[0].replace("/", "");
            containerNameList.add(containerName);
        }
        return containerNameList;
    }

    public static Container getInspectContainerById(String containerId){
        InspectContainerResponse exec = dockerClient.inspectContainerCmd(containerId).exec();
        System.out.println(exec);
        return null;
    }

    public static void execCMD(String containerId, String... cmd){
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        ByteArrayOutputStream stderr = new ByteArrayOutputStream();

        ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withCmd(cmd)
                .exec();
        try {
            dockerClient.execStartCmd(execCreateCmdResponse.getId()).exec(
                    new ExecStartResultCallback(stdout, stderr)).awaitCompletion();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(stdout.toString());


        return;
    }

    public static String getContainerIdByName(String containerName) {
        try {
//            String containerId = "";
//            Object object = imageList();
//            JSONArray jsonArray = JSONArray.t(object);
//            for (int i = 0; i < jsonArray.size(); i++) {
//                String name = jsonArray.getJSONObject(i).getString("names");
//                name = name.replace("[\"/", "").replace("\"]", "");
//                if (!StringUtils.isEmpty(name) && name.equals(containerName)) {
//                    containerId = jsonArray.getJSONObject(i).getString("id");
//                }
//            }
            return "1";
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    public static String getRandomPort(){
        return String.valueOf(RandomUtil.randomInt(8001, 65535));
    }


}

package com.sunzy.vulfocus.utils;

import com.alibaba.fastjson.JSONArray;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.InspectExecResponse;
import com.github.dockerjava.api.command.ListImagesCmd;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.Info;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.okhttp.OkDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DockerTools {

    private static DockerClient dockerClient = null;

    @Bean
    public void connectDocker() {
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


    public static String createContainer(String imageName, String containerName, HostConfig hostConfig, List<String> cmd){
        CreateContainerResponse container = dockerClient.createContainerCmd(imageName)
                .withName(containerName)
                .withHostConfig(hostConfig)
                .withCmd(cmd)
                .exec();
        return container.getId();
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




    public static List<Container> containerList(){
        List<Container> containerList = dockerClient.listContainersCmd().withShowAll(true).exec();
        return containerList;
    }

    public static List<String> getContainerNameList(List<Container> containerList) {
        List<String> containerNameList = new ArrayList<>();
        for (Container container : containerList) {
            String containerName = container.getNames()[0].replace("/", "");
            containerNameList.add(containerName);
        }
        return containerNameList;
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




}

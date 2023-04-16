package com.sunzy.vulfocus.utils;

import cn.hutool.core.util.RandomUtil;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.*;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.github.dockerjava.okhttp.OkDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.sunzy.vulfocus.model.dto.NetworkDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.*;

@Component
@Slf4j
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

    /**
     * 测试连接情况
     * @param dockerClient
     * @return
     */
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

    /**
     * 指定端口运行容器
     * @param imageName
     * @param ports
     * @return
     */
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
//        dockerClient.startContainerCmd(id).exec();
        return id;
    }

    public static Object startContainer(String containerID) {
        return dockerClient.startContainerCmd(containerID).exec();
    }

    public static Object restartContainer(String containerID) {
        return dockerClient.restartContainerCmd(containerID).exec();
    }

    public static void deleteContainer(String containerID) {
        // 必须先停止容器 再删除容器
        stopContainer(containerID);
        dockerClient.removeContainerCmd(containerID).exec();
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

    /**
     * 执行单条命令 形如 touch /tmp/flag{this is flag}
     * @param containerId
     * @param cmd
     */
    public static void execCMD(String containerId, String cmd){
        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
        ByteArrayOutputStream stderr = new ByteArrayOutputStream();

        String[] cmds = cmd.split(" ");
        ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd(containerId)
                .withAttachStdout(true)
                .withAttachStderr(true)
                .withCmd(cmds[0], cmds[1])
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


    public static List<Network> getNetworkList(){
        return dockerClient.listNetworksCmd().exec();
    }

    /**
     * 创建网卡
     * @param networkDTO
     * @return
     */
    public static Network createNetwork(NetworkDTO networkDTO){
        Network.Ipam ipam = new Network.Ipam();

        Network.Ipam.Config ipamConfig = new Network.Ipam.Config();
        ipamConfig = ipamConfig
                .withSubnet(networkDTO.getNetWorkSubnet())
                .withGateway(networkDTO.getNetWorkGateway());

        ipam = ipam.withConfig(ipamConfig);

        CreateNetworkResponse network = dockerClient.createNetworkCmd()
                .withName(networkDTO.getNetWorkName())
                .withDriver(networkDTO.getNetWorkDriver())
                .withIpam(ipam)
                .exec();
        List<Network> networkList = dockerClient.listNetworksCmd().withIdFilter(network.getId()).exec();
        if(networkList.size() > 0){
            return networkList.get(0);
        }
        return null;
    }


    /**
     * 根据id获取网卡信息
     * @param networkId
     * @return
     */
    public static Network getNetworkById(String networkId){
        List<Network> exec = dockerClient.listNetworksCmd().withIdFilter(networkId).exec();
        return exec.get(0);
    }

    public static void removeNetworkById(String networkId){
        dockerClient.removeNetworkCmd(networkId).exec();
    }

    public static BuildImageResultCallback callback = new BuildImageResultCallback() {
        @Override
        public void onNext(BuildResponseItem item) {
            System.out.println("" + item.toString());
            super.onNext(item);
        }
    };


    public static InspectImageResponse buidImageByFile(File imageFile, String imageName){
//        File file = new File("E:\\Sunzh\\java\\vulfocus\\src\\main\\resources\\dockerfile\\demo");
        String imageId = dockerClient.buildImageCmd(imageFile).withTags(Collections.singleton(imageName)).exec(callback).awaitImageId();
        return getImageByName(imageName);
    }

    public static void pullImageByName2(String imageName, PullImageResultCallback callback) throws InterruptedException {
        dockerClient.pullImageCmd(imageName).exec(callback).awaitCompletion();
    }


    public static boolean pullImageByName(String imageName) throws InterruptedException {
        PullImageResultCallback callback = new PullImageResultCallback() {
            @Override
            public void onNext(PullResponseItem item) {
//                if(item != null && item.getProgressDetail() != null){
                    log.info(item.toString());
//                }
                super.onNext(item);
            }
            @Override
            public void onError(Throwable throwable) {
                log.error("Failed to exec start:" + throwable.getMessage());
                super.onError(throwable);
            }
        };
        dockerClient.pullImageCmd(imageName).exec(callback).awaitCompletion();
        return true;
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

    // TODO
    public static String getLocalIp(){
        return "127.0.0.1";
    }


    /**
     * 获取8080-65535之间的随机端口
     * @return
     */
    public static String getRandomPort(){
        return String.valueOf(RandomUtil.randomInt(8080, 65535));
    }


}

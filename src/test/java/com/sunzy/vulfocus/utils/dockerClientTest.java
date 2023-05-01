package com.sunzy.vulfocus.utils;

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
import com.sunzy.vulfocus.utils.DockerTools;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayOutputStream;
import java.util.*;

@Slf4j
@SpringBootTest
public class dockerClientTest {
    private DockerClient dockerClient = null;

    @Test
    void testClient() {
        connectDocker();

        HostConfig hostConfig = new HostConfig();
//        List<String> cmd = new ArrayList<>();
//        cmd.add("echo hello docker");
//        String imageName = "alpine";
//        String demo = createContainer(imageName, "demo", hostConfig, cmd);
//        System.out.println(demo);
        List<Image> imageList = dockerClient.listImagesCmd().exec();
        for (Image image : imageList) {
            System.out.println(image);
        }

        List<Container> containerList = dockerClient.listContainersCmd().withShowAll(true).exec();
        for (Container container : containerList) {
            System.out.println(container);
        }
    }

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

    public String createContainer(String imageName, String containerName, HostConfig hostConfig, List<String> cmd){
        CreateContainerResponse container = dockerClient.createContainerCmd(imageName)
                .withName(containerName)
                .withHostConfig(hostConfig)
                .withCmd(cmd)
                .exec();
        return container.getId();
    }

    @Test
    void test1() {
        HostConfig hostConfig = new HostConfig();
        List<String> cmd = new ArrayList<>();
        cmd.add("echo hello hello");
        String imageName = "alpine";
        String id = DockerTools.createContainer(imageName, "demo2",  hostConfig,null, cmd);
        System.out.println(id);

    }

    @Test
    void testGetInspectContainerById(){
        DockerTools.getInspectContainerById("52c8e3935599");
    }

    @Test
    public void testGetContainerStatus() throws InterruptedException {
//        DockerClient dockerClient = DockerTools.getDockerClient();
//        List<Container> containerList = dockerClient.listContainersCmd().withShowAll(true).exec();
//        for (Container container : containerList) {
//            System.out.println(container);
//        }

//        dockerClient.execStartCmd("b10a97e9c85418d8a444bf752726f8ecbc9d8042752865576c07e74d5b53e55b");
//        ByteArrayOutputStream stdout = new ByteArrayOutputStream();
//        ByteArrayOutputStream stderr = new ByteArrayOutputStream();
//
//        ExecCreateCmdResponse execCreateCmdResponse = dockerClient.execCreateCmd("9bdfaec48122")
//                .withAttachStdout(true)
//                .withAttachStderr(true)
//                .withCmd("ping", "127.0.0.1", "-c", "3")
//                .exec();
//        dockerClient.execStartCmd(execCreateCmdResponse.getId()).exec(
//                new ExecStartResultCallback(stdout, stderr)).awaitCompletion();
//        System.out.println(stdout.toString());


//        DockerTools.execCMD("9bdfaec48122", "ping", "127.0.0.1", "-c", "3");
        DockerClient dockerClient = DockerTools.getDockerClient();

        Ports portBindings = new Ports();
//        portBindings.bind(new ExposedPort(80), Ports.Binding.bindPort(9004));
//        portBindings.bind(new ExposedPort(22), Ports.Binding.bindPort(2023));
//        String id = dockerClient.createContainerCmd("alpine:latest")
//                .withPortBindings(portBindings)
//                .withExposedPorts(new ExposedPort(22))
//                .exec()
//                .getId();
//
//        dockerClient.startContainerCmd(id).exec();
//        System.out.println("=======" + id);
        //     .withEnv(newEnvironment()
        //      .withValues(getMesosDNSEnvVars())
        //      .createEnvironment())
        //    .withCmd("-v=2", "-config=/etc/mesos-dns/config.json")
        //    .withExposedPorts(new ExposedPort(Integer.valueOf(DNS_PORT), InternetProtocol.UDP),
        //             new ExposedPort(Integer.valueOf(DNS_PORT), InternetProtocol.TCP))
        //    .withName(getName());

        Map<String, Integer> portMap = new HashMap<>();
        portMap.put("80", 9998);
        portMap.put("22", 1022);
        String s = DockerTools.runContainerWithPorts("nginx:latest", portMap);
        System.out.println(s);
    }

    @Test
    void testExec() {
        DockerTools.execCMD("2d84922dfff6", "touch /tmp/flag{this}");
    }

    @Test
    void testDelete() {
        DockerTools.deleteContainer("37ea8fd34383");
    }

    @Test
    void testListNetwork() {
        List<Network> networkList = DockerTools.getNetworkList();
        for (Network network : networkList) {
            // config=[Network.Ipam.Config(subnet=172.17.0.0/16, ipRange=null, gateway=172.17.0.1, networkID=null)]
            if(network.getIpam().getConfig().size() != 0){
                System.out.println(network.getIpam().getConfig().get(0).getSubnet());
            }
        }
    }

    @Test
    void testCreateNetwork() {
        NetworkDTO networkDTO = new NetworkDTO();
        networkDTO.setNetWorkName("demo");
        networkDTO.setNetWorkSubnet("192.168.2.0/24");
        networkDTO.setNetWorkGateway("192.168.2.1");
        networkDTO.setNetWorkScope("local");
        networkDTO.setNetWorkDriver("bridge");
        networkDTO.setEnableIpv6(false);



//        CreateNetworkResponse networkResponse = dockerClient.createNetworkCmd()
//                .withName("baeldung")
//                .withIpam(new Network.Ipam()
//                        .withConfig(ipam)
//                .withDriver("bridge").exec();
        DockerTools.createNetwork(networkDTO);
    }

    @Test
    void testGetNetworkById() {
        Network bafcc05e86b6 = DockerTools.getNetworkById("bafcc05e86b6");
        System.out.println(bafcc05e86b6);
    }

    @Test
    void testRemoveNetworkById() {
        DockerTools.removeNetworkById("bafcc05e86b6");
    }


    @Test
    void testBuidImageByFile(){
//        DockerTools.buidImageByFile();
    }

    @Test
    void testGetImageByName() {
        InspectImageResponse tesada = DockerTools.getImageByName("tesada");
//        assert tesada != null;
    }

    PullImageResultCallback callback = new PullImageResultCallback() {
        @Override
        public void onNext(PullResponseItem item) {
//                if(item != null && item.getProgressDetail() != null){
//                    log.info(item.getProgressDetail().toString());
//                }
            log.info(item.toString());
            super.onNext(item);
        }
        @Override
        public void onError(Throwable throwable) {
            log.error("Failed to exec start:" + throwable.getMessage());
            super.onError(throwable);
        }
    };
    @Test
    void testPutImageByName() throws InterruptedException {
        DockerTools.pullImageByName2("redis:2.6", callback);
    }

    @Test
    void testGetContainerById() {
        Container containerById = DockerTools.getContainerById("62785bc5c89304b57c892aaac510341f662c784b70b1fbdf768a71d129dce81a");
        System.out.println(containerById);
    }

    @Test
    void testInspectImageByName() throws InterruptedException {
        InspectImageResponse inspectImageResponse = DockerTools.inspectImage("alpine:latest");
        System.out.println(inspectImageResponse);

    }


    @Test
    void testGetContainersByName() throws InterruptedException {
        ArrayList<String> name = new ArrayList<>();

        name.add("56d186f2-d64c-48a1-8a21-f6630bd707ef-7h4vhg3c58w0-1");
        name.add("56d186f2-d64c-48a1-8a21-f6630bd707ef-t3a2i35fqv4-1");
        ArrayList<Container> containersByName = DockerTools.getContainersByName(name);
        for (Container container : containersByName) {
            Map<String, String> labels = container.getLabels();
            System.out.println(labels.get("com.docker.compose.service"));
        }
    }

}

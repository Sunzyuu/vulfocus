package com.sunzy.vulfocus.utils;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.command.ExecCreateCmdResponse;
import com.github.dockerjava.api.command.ListContainersCmd;
import com.github.dockerjava.api.command.ListImagesCmd;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.core.command.ExecStartResultCallback;
import com.github.dockerjava.okhttp.OkDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import com.sunzy.vulfocus.utils.DockerTools;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.ByteArrayOutputStream;
import java.util.*;

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
}

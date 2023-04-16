package com.sunzy.vulfocus.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.command.PullImageResultCallback;
import com.github.dockerjava.api.model.*;
import com.sunzy.vulfocus.common.*;
import com.sunzy.vulfocus.model.dto.ImageDTO;
import com.sunzy.vulfocus.model.dto.UserDTO;
import com.sunzy.vulfocus.model.po.ContainerVul;
import com.sunzy.vulfocus.model.po.ImageInfo;
import com.sunzy.vulfocus.model.po.TaskInfo;
import com.sunzy.vulfocus.mapper.TaskInfoMapper;
import com.sunzy.vulfocus.model.po.UserUserprofile;
import com.sunzy.vulfocus.service.*;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunzy.vulfocus.utils.DockerTools;
import com.sunzy.vulfocus.utils.GetIdUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

import static com.sunzy.vulfocus.common.ErrorClass.ImagePullFailedException;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author sunzy
 * @since 2023-04-01
 */
@Slf4j
@Service
@Transactional
public class TaskInfoServiceImpl extends ServiceImpl<TaskInfoMapper, TaskInfo> implements TaskInfoService {

    @Resource
    private AmqpTemplate amqpTemplate;

    @Resource
    private SysLogService logService;

    @Resource
    private UserUserprofileService userService;

    @Resource
    private ContainerVulService containerService;

    @Resource
    private ImageInfoService imageService;

    @Override
    public String createImageTask(ImageInfo imageInfo, UserDTO user, File imageFile) {
        Integer userId = user.getId();
        String taskId = createCreateImageTask(imageInfo, user);
        TaskInfo taskInfo = new TaskInfo();
        String imageName = imageInfo.getImageName();
//        String imageVulName = imageInfo.getImageVulName();
//        String imageDesc = imageInfo.getImageDesc();
        Double rank = imageInfo.getRank();
        Result taskMsg = Result.ok();
        StringBuffer imagePort = new StringBuffer();
        if (user.getSuperuser()) {
            taskInfo = getById(taskId);
            // TODO create image by file
            if (imageFile != null) {
                try {
                    // 创建镜像
                    InspectImageResponse image = DockerTools.buidImageByFile(imageFile, imageName);
//                    InspectImageResponse image = DockerTools.getImageByName(imageName);
                    List<String> repoTags = image.getRepoTags();
                    if(repoTags.size() == 0){
                        try {
                            DockerTools.removeImages(image.getId());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        taskMsg =  Result.fail("文件镜像 Tag 不能为空");
                    } else {
                        if (image.getConfig() != null) {
                            ExposedPort[] exposedPorts = image.getConfig().getExposedPorts();
                            for (int i = 0; i < exposedPorts.length; i++) {
                                if (i != exposedPorts.length - 1) {
                                    imagePort.append(exposedPorts[i].getPort()).append(",");
                                } else {
                                    imagePort.append(exposedPorts[i].getPort());
                                }
                            }
                        }
                        String portsStr = imagePort.toString();
//                        imageInfo = imageService.getById(imageInfo.getImageId());
//                        if(imageInfo == null){
//                            imageInfo = new ImageInfo();
//                        }
                        imageInfo.setImagePort(portsStr);
                        imageInfo.setRank(rank > 5 || rank < 0.5 ? 2.5 : rank);
                        imageInfo.setOk(true);
                        imageService.save(imageInfo);
                        taskInfo.setTaskName("拉取镜像:" + imageName);
                        taskInfo.setTaskStatus(3);
                        taskMsg = Result.ok(imageName + "添加成功");
                    }
                } catch (Exception e){
                    e.printStackTrace();

                } finally {
                    taskInfo.setTaskMsg(JSON.toJSONString(taskMsg));
                    taskInfo.setUpdateDate(LocalDateTime.now());
                    updateById(taskInfo);
                }

            } else if (!StrUtil.isBlank(imageName)) {
                //
                SpringUtil.getApplicationContext().getBean(TaskInfoServiceImpl.class).createImage(taskId);
            } else {
                return "imageName is empty!";
            }
            // log
//            imageInfo = imageService.query().eq("image_name", imageName).one();
            logService.sysImageLog(user, imageInfo, "创建");

        } else {
            taskInfo = query().eq("task_id", taskId).one();
            Result msg = Result.fail("权限不足");
            taskInfo.setTaskMsg(JSON.toJSONString(msg));
            taskInfo.setTaskStatus(3);
            taskInfo.setUpdateDate(LocalDateTime.now());
            updateById(taskInfo);
//            LambdaQueryWrapper<TaskInfo> wrapper = new LambdaQueryWrapper<>();
//            wrapper.eq(true, TaskInfo::getTaskId, taskId);
//            update(taskInfo, wrapper);
        }
        return taskId;
    }

    /**
     * 创建运行容器任务
     *
     * @param containerVul 漏洞容器对象
     * @param user         用户信息
     * @return taskId
     */
    private String createRunContainerTask(ContainerVul containerVul, UserDTO user) {
        String imageId = containerVul.getImageIdId();
        ImageInfo imageInfo = imageService.query().eq("image_id", imageId).one();
        String imageName = imageInfo.getImageName();
        String imagePort = imageInfo.getImagePort();
        Integer userId = user.getId();
        Map<String, Object> args = new HashMap<>();
        args.put("imageName", imageName);
        args.put("user_id", userId);
        args.put("image_port", imagePort);
        args.put("container_id", containerVul.getContainerId());

        TaskInfo taskInfo = null;
        if ("running".equals(containerVul.getContainerStatus())) {
            String vulPort = containerVul.getContainerPort();
            String vulHost = containerVul.getVulHost();
            HashMap<String, Object> data = new HashMap<>();
            data.put("host", vulHost);
            data.put("port", vulPort);
            data.put("id", containerVul.getContainerId());
            Result ok = Result.ok(data);
            LambdaQueryWrapper<TaskInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(true, TaskInfo::getOperationArgs, JSON.toJSONString(args));
            wrapper.eq(true, TaskInfo::getTaskMsg, JSON.toJSONString(ok));
            wrapper.eq(true, TaskInfo::getOperationType, "2");
            wrapper.eq(true, TaskInfo::getTaskName, "运行容器:" + imageName);
            wrapper.eq(true, TaskInfo::getUserId, userId);
            taskInfo = getOne(wrapper);
        }

        if (taskInfo == null) {
            LambdaQueryWrapper<TaskInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(true, TaskInfo::getOperationArgs, JSON.toJSONString(args));
            wrapper.eq(true, TaskInfo::getTaskMsg, "");
            wrapper.eq(true, TaskInfo::getTaskStatus, 1);
            wrapper.eq(true, TaskInfo::getOperationType, "2");
            wrapper.eq(true, TaskInfo::getTaskName, "运行容器:" + imageName);
            wrapper.eq(true, TaskInfo::getUserId, userId);
            taskInfo = getOne(wrapper);
        }

        if (taskInfo == null) {
            taskInfo = new TaskInfo();
            String taskId = IdUtil.simpleUUID();
            taskInfo.setTaskId(taskId);
            taskInfo.setTaskName("运行容器:" + imageName);
            taskInfo.setUserId(userId);
            taskInfo.setTaskStatus(1);
            taskInfo.setOperationType("2");
            taskInfo.setTaskMsg("");
            taskInfo.setOperationArgs(JSON.toJSONString(args));
            taskInfo.setCreateDate(LocalDateTime.now());
            taskInfo.setUpdateDate(LocalDateTime.now());
            save(taskInfo);
        }

//        task_msg = R.ok(data={"host": vul_host, "port": vul_port, "id": str(container_vul.container_id)})
//        task_info = TaskInfo.objects.filter(operation_args=json.dumps(args), task_msg=json.dumps(task_msg),
//                operation_type=2, task_name="运行容器：" + image_name, user_id=user_id).first()
//        if not task_info:
//        task_info = TaskInfo.objects.filter(operation_args=json.dumps(args), task_msg="", task_status=1, user_id=user_id,
//                operation_type=2, task_name="运行容器：" + image_name).first()
//        if not task_info:
//        task_info = TaskInfo(task_name="运行容器：" + image_name, user_id=user_id, task_status=1,
//                operation_type=2, operation_args=json.dumps(args), task_msg="", create_date=timezone.now(),
//                update_date=timezone.now())
//        task_info.save()
//        return str(task_info.task_id)


        return taskInfo.getTaskId();
    }

    /**
     * 创建容器任务
     *
     * @param containerVul 容器对象
     * @param user         用户
     * @return taskId
     */
    @Override
    public String createContainerTask(ContainerVul containerVul, UserDTO user) throws Exception {
        String imageIdId = containerVul.getImageIdId();
        ImageInfo imageInfo = imageService.query().eq("image_id", imageIdId).one();
        if (imageInfo == null) {
            throw ErrorClass.ImageNotExistsException;
        }
        String taskId = createRunContainerTask(containerVul, user);
        Integer userId = user.getId();
        if (user.getSuperuser() || userId == containerVul.getUserId()) {
            ImageDTO imageDTO = imageService.handleImageDTO(imageInfo, user);
            logService.sysContainerLog(user, imageDTO, containerVul, "启动");
            int countdown = 30 * 60;
            // TODO 倒计时关闭
            SpringUtil.getApplicationContext().getBean(TaskInfoServiceImpl.class).runContainer(containerVul.getContainerId(), user, taskId, countdown);

        } else {
            TaskInfo taskInfo = query().eq("task_id", taskId).one();
            taskInfo.setTaskMsg(JSON.toJSONString(Result.build("权限不足", null)));
            taskInfo.setTaskStatus(3);
            taskInfo.setUpdateDate(LocalDateTime.now());
            LambdaQueryWrapper<TaskInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(true, TaskInfo::getTaskId, taskId);
            update(taskInfo, wrapper);
        }
        return taskId;
    }

    @Override
    public String stopContainerTask(ContainerVul containerVul, UserDTO user) throws Exception {
        log.info("===================第一步");
        Integer userId = user.getId();
        String taskId = createStopContainerTask(containerVul, user);
        if (user.getSuperuser() || userId.equals(containerVul.getUserId())) {
            ImageInfo imageInfo = imageService.query().eq("image_id", containerVul.getImageIdId()).one();
            logService.sysContainerLog(user, imageInfo, containerVul, "停止");
            log.info("===================第二步");
//            stopContainer(taskId);
            SpringUtil.getApplicationContext().getBean(TaskInfoServiceImpl.class).stopContainer(taskId);

        } else {
            TaskInfo taskInfo = query().eq("task_id", taskId).one();
            taskInfo.setTaskMsg(JSON.toJSONString(Result.build("权限不足", null)));
            taskInfo.setTaskStatus(3);
            taskInfo.setUpdateDate(LocalDateTime.now());
            LambdaQueryWrapper<TaskInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(true, TaskInfo::getTaskId, taskId);
            update(taskInfo, wrapper);
        }
        log.info("===================第三步");
        return taskId;
    }

    @Override
    public String deleteContainerTask(ContainerVul containerVul, UserDTO user) throws Exception {
        Integer userId = user.getId();
        String taskId = createDeleteContainerTask(containerVul, user);
        if (user.getSuperuser() || userId.equals(containerVul.getUserId())) {
            ImageInfo imageInfo = imageService.query().eq("image_id", containerVul.getImageIdId()).one();
            logService.sysContainerLog(user, imageInfo, containerVul, "删除");
            SpringUtil.getApplicationContext().getBean(TaskInfoServiceImpl.class).deleteContainer(taskId);
        } else {
            TaskInfo taskInfo = query().eq("task_id", taskId).one();
            taskInfo.setTaskMsg(JSON.toJSONString(Result.build("权限不足", null)));
            taskInfo.setTaskStatus(3);
            taskInfo.setUpdateDate(LocalDateTime.now());
            LambdaQueryWrapper<TaskInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(true, TaskInfo::getTaskId, taskId);
            update(taskInfo, wrapper);
        }
        return taskId;
    }

    @Override
    public Result getTask(String taskId) {
        TaskInfo taskInfo = query().eq("task_id", taskId).one();
        if (taskInfo.getTaskStatus() == 1) {
            return Result.running("执行中", taskId);
        }
        taskInfo.setIsShow(true);
        LambdaQueryWrapper<TaskInfo> updateWrapper = new LambdaQueryWrapper<>();
        updateWrapper.eq(true, TaskInfo::getTaskId, taskId);
        update(taskInfo, updateWrapper);
        String taskMsg = taskInfo.getTaskMsg();
        Map msg = new HashMap<>();
        if (!StrUtil.isBlank(taskMsg)) {
            msg = JSON.parseObject(taskMsg, Map.class);
            if ((Integer) msg.get("status") == 200) {
//                if(msg.get("data") != null){
//                }
                return new Result(200, "", msg);
            } else {
                return new Result((Integer) msg.get("status"), "", msg);
            }
        }
        return Result.ok();
    }

    @Override
    public Result getBatchTask(String taskIds) {
        if (StrUtil.isBlank(taskIds)) {
            return Result.ok();
        }
        System.out.println(taskIds);
        String[] taskIdList = taskIds.split(",");
        System.out.println(taskIdList);
        List<TaskInfo> taskInfos = listByIds(Arrays.asList(taskIdList));
        System.out.println(taskInfos);
        HashMap<String, Map> result = new HashMap<>();
        for (TaskInfo taskInfo : taskInfos) {
            double progress = 0.0;
            // TODO 将任务id加入到redis中获取其执行到进度
            HashMap<String, Object> data = new HashMap<>();
            data.put("status", taskInfo.getTaskStatus());
            data.put("data", JSON.parseObject(taskInfo.getTaskMsg(), Map.class));
            data.put("progress", progress);
            result.put(taskInfo.getTaskId(), data);
        }
        return Result.ok(result);
    }

    // TODO 实时获取任务执行进度 get_task_progress 针对镜像下载时的功能实现
    @Async
    public void deleteContainer(String taskId) {
        LambdaQueryWrapper<TaskInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(true, TaskInfo::getTaskId, taskId);
        wrapper.eq(true, TaskInfo::getTaskStatus, 1);
        TaskInfo taskInfo = getOne(wrapper);
        if (taskInfo == null) {
            return;
        }
        String operationArgs = taskInfo.getOperationArgs();
        Map<String, String> map = JSON.parseObject(operationArgs, Map.class);
        String containerId = map.get("container_id");
        ContainerVul containerVul = containerService.query().eq("container_id", containerId).one();
        Result msg = Result.ok("删除成功");
        if (containerVul != null) {
            String dockerContainerId = containerVul.getDockerContainerId();
            try {
                DockerTools.deleteContainer(dockerContainerId);
            } catch (Exception e) {
                msg = Result.fail("删除失败，服务器内部错误");
            } finally {
                log.info("删除容器: {}", dockerContainerId);
                containerVul.setContainerStatus("delete");
                containerVul.setDockerContainerId("");
                containerVul.setVulPort("");
                LambdaQueryWrapper<ContainerVul> updateWrapperContainer = new LambdaQueryWrapper<>();
                updateWrapperContainer.eq(true, ContainerVul::getContainerId, containerId);
                containerService.update(containerVul, updateWrapperContainer);
            }
        }
        taskInfo.setTaskStatus(3);
        taskInfo.setTaskMsg(JSON.toJSONString(msg));
        taskInfo.setUpdateDate(LocalDateTime.now());
        LambdaQueryWrapper<TaskInfo> updateWrapperTask = new LambdaQueryWrapper<>();
        updateWrapperTask.eq(true, TaskInfo::getTaskId, taskId);
        update(taskInfo, updateWrapperTask);
    }

    @Async
    public void stopContainer(String taskId) {
        log.info("===================第四步");
        LambdaQueryWrapper<TaskInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(true, TaskInfo::getTaskId, taskId);
        wrapper.eq(true, TaskInfo::getTaskStatus, 1);
        TaskInfo taskInfo = getOne(wrapper);
        if (taskInfo == null) {
            return;
        }
        String operationArgs = taskInfo.getOperationArgs();
        Map<String, String> map = JSON.parseObject(operationArgs, Map.class);
        String containerId = map.get("container_id");
        ContainerVul containerVul = containerService.query().eq("container_id", containerId).one();
        Result msg = Result.ok("停止成功");
        if (containerVul != null && !"stop".equals(containerVul.getContainerStatus())) {
            String dockerContainerId = containerVul.getDockerContainerId();
            try {
                DockerTools.stopContainer(dockerContainerId);
                containerVul.setContainerStatus("stop");
                LambdaQueryWrapper<ContainerVul> updateWrapperContainer = new LambdaQueryWrapper<>();
                updateWrapperContainer.eq(true, ContainerVul::getContainerId, containerId);
                containerService.update(containerVul, updateWrapperContainer);
            } catch (Exception e) {
                msg = Result.fail("停止失败，服务器内部错误");
            }
        }
        taskInfo.setTaskStatus(3);
        taskInfo.setTaskMsg(JSON.toJSONString(msg));
        taskInfo.setUpdateDate(LocalDateTime.now());
        LambdaQueryWrapper<TaskInfo> updateWrapperTask = new LambdaQueryWrapper<>();
        updateWrapperTask.eq(true, TaskInfo::getTaskId, taskId);
        update(taskInfo, updateWrapperTask);
        log.info("===================第五步");
    }


    /**
     * 启动docker容器
     *
     * @param containerId id
     * @param user        user
     * @param taskId      taskID
     * @param countdown   倒计时
     * @return 停止容器任务id
     */
    @Async
    public void runContainer(String containerId, UserDTO user, String taskId, int countdown) throws Exception {
        ContainerVul containerVul = containerService.query().eq("container_id", containerId).one();
        if (containerVul == null) {
            throw ErrorClass.ContainerNotExistsException;
        }
        String dockerContainerId = null;
        UserUserprofile userInfo = userService.getById(user.getId());
        dockerContainerId = containerVul.getDockerContainerId();
        ImageInfo imageInfo = imageService.query().eq("image_id", containerVul.getImageIdId()).one();
        String imageName = imageInfo.getImageName();
        String imagePort = imageInfo.getImagePort();
        Integer userId = userInfo.getId();

        Result msg = null;
        /**
         * 创建启动任务
         */
        HashMap<String, Object> args = new HashMap<>();
        args.put("imageName", imageName);
        args.put("userId", userId);
        args.put("imagePort", imagePort);
        TaskInfo taskInfo = query().eq("task_id", taskId).one();
        String command = "";
        String vulFlag = containerVul.getContainerFlag();
        String containerPort = containerVul.getContainerPort();

        String vulPort = "";
        if (containerVul.getVulPort() != null) {
            vulPort = containerVul.getVulPort();
        }
        String vulHost = containerVul.getVulHost();
        Container container = null;
        if (!StrUtil.isBlank(dockerContainerId)) {
            CheckResp checkResp = checkContainer(dockerContainerId);
            if (checkResp.isFlag()) {
                container = checkResp.getContainer();
                vulFlag = containerVul.getContainerFlag();
            }
        }

        // 容器被删除，此时要创建一个容器
        if (container == null) {
            String[] portList = imagePort.split(",");
            ArrayList<String> randomList = new ArrayList<>();
            HashMap<String, Integer> portDict = new HashMap<>();
            for (String port : portList) {
                String randomPort = "";
                for (int i = 0; i < 20; i++) {
                    randomPort = DockerTools.getRandomPort();
                    if (randomList.contains(randomPort) || containerService.query().eq("container_port", randomPort).one() != null) {
                        continue;
                    }
                    break;
                }
                if (StrUtil.isBlank(randomPort)) {
                    msg = Result.fail("端口无效");
                    break;
                }
                randomList.add(randomPort);
                portDict.put(port + "/tcp", Integer.valueOf(randomPort));
            }
            // 端口重复无法创建
            if (msg != null) {
                taskInfo.setTaskMsg(JSON.toJSONString(msg));
                taskInfo.setUpdateDate(LocalDateTime.now());
                taskInfo.setTaskStatus(4);
                save(taskInfo);
//                return taskInfo.getTaskId();
            }
            // 记录端口映射
            // {"3306": "24471", "80": "29729"}
            // {"3306":"22113","8080":"12345"}
            vulPort = JSON.toJSONString(portDict);
            HashMap<String, Integer> vulPorts = new HashMap<>();
            Set<Map.Entry<String, Integer>> entries = portDict.entrySet();
            for (Map.Entry<String, Integer> entry : entries) {
                String port = entry.getKey().split("/")[0];
                Integer tmpRandomPort = entry.getValue();
                vulPorts.put(port, tmpRandomPort);
            }
            try {
                // 只创建不启动
                dockerContainerId = DockerTools.runContainerWithPorts(imageName, vulPorts);
                container = DockerTools.getContainerById(dockerContainerId);
            } catch (Exception e) {
                // 修改任务状态
                msg = Result.build("镜像不存在", null);
                taskInfo.setTaskMsg(JSON.toJSONString(msg));
                taskInfo.setUpdateDate(LocalDateTime.now());
                taskInfo.setTaskStatus(4);
                save(taskInfo);
                throw new RuntimeException(e);
            }
            vulFlag = "flag{" + UUID.randomUUID().toString() + "}";
            if (!StrUtil.isBlank(containerVul.getContainerFlag())) {
                vulFlag = containerVul.getContainerFlag();
            }
            command = "touch /tmp/" + vulFlag;
            vulHost = DockerTools.getLocalIp();
        }   // 容器存在
        LocalDateTime taskStartDate = LocalDateTime.now();
        LocalDateTime taskEndDate = null;
        if (countdown >= 60) {
            taskEndDate = taskStartDate.plusSeconds(countdown);
        } else if (countdown == 0) {

        } else {
            countdown = SystemConstants.DOCKER_CONTAINER_TIME;
            taskEndDate = taskStartDate.plusSeconds(countdown);
        }
        assert container != null;
        if (container.getState().equals("running")) {
            HashMap<String, Object> data = new HashMap<>();
            data.put("host", vulHost);
            data.put("port", vulPort);
            data.put("id", containerId);
            data.put("status", "running");
            data.put("start_data", taskStartDate.toInstant(ZoneOffset.ofHours(8)).toEpochMilli());
            data.put("end_data", taskEndDate != null ? taskEndDate.toInstant(ZoneOffset.ofHours(8)).toEpochMilli() : 0);
            LambdaQueryWrapper<TaskInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(true, TaskInfo::getUserId, userId);
            wrapper.eq(true, TaskInfo::getTaskMsg, JSON.toJSONString(data));
            wrapper.eq(true, TaskInfo::getOperationType, 2);
            wrapper.eq(true, TaskInfo::getOperationArgs, JSON.toJSONString(args));
            wrapper.eq(true, TaskInfo::getTaskEndDate, taskEndDate);
            wrapper.eq(true, TaskInfo::getTaskName, "运行容器：" + imageName);
            TaskInfo searchTaskInfo = getOne(wrapper);

//            containerVul.setContainerStatus(container.getState());
//            containerVul.setDockerContainerId(dockerContainerId);
//            containerVul.setVulHost(vulHost);
//            containerVul.setVulPort(vulPort);
//            containerVul.setContainerFlag(vulFlag);
//            containerVul.setContainerPort(containerPort);
//            containerVul.setCreateDate(LocalDateTime.now());
//            containerVul.setUserId(userId);
//            containerVul.setIScheck(false);
//            LambdaQueryWrapper<ContainerVul> updateWrapper = new LambdaQueryWrapper<>();
//            updateWrapper.eq(true, ContainerVul::getContainerId, containerId);
//            containerService.update(containerVul, updateWrapper);
            if (searchTaskInfo == null) {
//                taskInfo = new TaskInfo();
                taskInfo.setTaskId(taskId);
                taskInfo.setTaskStatus(3);
                taskInfo.setTaskMsg(JSON.toJSONString(data));
                taskInfo.setOperationArgs(JSON.toJSONString(args));
                taskInfo.setUpdateDate(LocalDateTime.now());
                taskId = taskInfo.getTaskId();
                LambdaQueryWrapper<TaskInfo> updateWrapperTask = new LambdaQueryWrapper<>();
                updateWrapperTask.eq(true, TaskInfo::getTaskId, taskId);
                update(taskInfo, updateWrapperTask);
            } else {
                LambdaQueryWrapper<TaskInfo> removeWapper = new LambdaQueryWrapper<>();
                removeWapper.eq(true, TaskInfo::getTaskId, taskId);
                remove(removeWapper);
                searchTaskInfo.setTaskId(taskId);
                searchTaskInfo.setUpdateDate(LocalDateTime.now());
                searchTaskInfo.setTaskStatus(3);
                save(searchTaskInfo);
            }

        } else {
            DockerTools.startContainer(dockerContainerId);
            // 写入flag
            msg = dockerContainerRun(container, command);
            if (msg != null && msg.getStatus() == SystemConstants.HTTP_ERROR) {
                try {
                    DockerTools.deleteContainer(container.getId());
                } catch (Exception ignored) {
                    log.info("删除容器失败！");
                }
                taskInfo.setTaskStatus(4);
            } else {
                HashMap<String, Object> data = (HashMap<String, Object>) msg.getData();
                String status = (String) data.get("status");
                data.put("host", vulHost);
                data.put("port", vulPort);
                data.put("id", containerId);
                data.put("start_data", taskStartDate.toInstant(ZoneOffset.ofHours(8)).toEpochMilli());
                data.put("end_data", taskEndDate != null ? taskEndDate.toInstant(ZoneOffset.ofHours(8)).toEpochMilli() : 0);

                containerVul.setContainerStatus(status);
                containerVul.setDockerContainerId(dockerContainerId);
                containerVul.setVulHost(vulHost);
                containerVul.setVulPort(vulPort);
                containerVul.setContainerFlag(vulFlag);
                containerVul.setContainerPort(containerPort);
                containerVul.setCreateDate(LocalDateTime.now());
                containerVul.setUserId(userId);
                containerVul.setIScheck(false);
                LambdaQueryWrapper<ContainerVul> updateWrapper = new LambdaQueryWrapper<>();
                updateWrapper.eq(true, ContainerVul::getContainerId, containerVul.getContainerId());
                containerService.update(containerVul, updateWrapper);

                taskInfo.setTaskStartDate(LocalDateTime.now());
                taskInfo.setTaskEndDate(LocalDateTime.now());
                taskInfo.setTaskStatus(3);
            }

            taskInfo.setTaskMsg(JSON.toJSONString(msg));
            taskInfo.setUpdateDate(LocalDateTime.now());

            LambdaQueryWrapper<TaskInfo> updateWrapperTask = new LambdaQueryWrapper<>();
            updateWrapperTask.eq(true, TaskInfo::getTaskId, taskId);
            update(taskInfo, updateWrapperTask);
        }
        log.info("启动漏洞容器成功，任务ID：" + taskId);
        // 创建关闭容器任务 半小时后关闭
        String stopContainerTaskId = createStopContainerTask(containerVul, user);
        // 发送到RabbitMQ的死信队列
        log.info("向死信队列发送任务id");
        amqpTemplate.convertAndSend(RabbitConstants.DLX_EXCHANGE,
                RabbitConstants.DLX_ROUTING_KEY,
                stopContainerTaskId);
//        return taskId;
    }


    private StringBuffer portListToStr(ArrayList<String> randomList) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < randomList.size(); i++) {
            if (i != randomList.size() - 1) {
                sb.append(randomList.get(i)).append(",");
            } else {
                sb.append(randomList.get(i));
            }
        }
        return sb;
    }

    /**
     * 检测container是否运行正常
     *
     * @param containerId 容器id
     * @return 检测结果
     */
    private CheckResp checkContainer(String containerId) {
        try {
            Container container = DockerTools.getContainerById(containerId);
            return new CheckResp(true, container);
        } catch (Exception e) {
            return new CheckResp(false, null);
        }
    }


    private String createStopContainerTask(ContainerVul containerVul, UserDTO user) {
        return createBaseContainerTask(containerVul, user, 3);
    }

    private String createDeleteContainerTask(ContainerVul containerVul, UserDTO user) {
        return createBaseContainerTask(containerVul, user, 4);
    }


    /**
     * 容器启动
     *
     * @param container 容器对象
     * @param command   启动命令
     * @return 结果
     */
    private Result dockerContainerRun(Container container, String command) {
        container = DockerTools.getContainerById(container.getId());
        HashMap<String, Object> data = new HashMap<>();
        if (StrUtil.isBlank(command)) {
            assert container != null;
            data.put("status", container.getState());
            return Result.ok(data);
        }
        for (int i = 0; i < SystemConstants.DOCKER_CONTAINER_TIME; i++) {
            if (container.getState().equals("running") && !StrUtil.isBlank(command)) {
                DockerTools.execCMD(container.getId(), command);
                break;
            }
            container = DockerTools.getContainerById(container.getId());
            /* else if (container.getStatus().contains("exited")) {
                continue;
            }*/
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        data.put("status", container.getState());
        if (container.getState().equals("running")) {
            return Result.ok(data);
        } else {
            return Result.fail("漏洞容器启动失败", data);
        }
    }


    @Transactional
    @Async
    void createImage(String taskId) {
        System.out.println("create image ...");
        TaskInfo taskInfo = query().eq("task_id", taskId).one();
        if (taskInfo == null) {
            return;
        }
        String operationArgs = taskInfo.getOperationArgs();
        Map args = JSON.parseObject(operationArgs, Map.class);
        String imageName = (String) args.get("image_name");

//        ImageDTO imageDTO = JSON.parseObject(operationArgs, Map.class);
//        ImageDTO imageDTO = new ImageDTO();
//        String imageName = imageDTO.getImageName();
        ImageInfo imageInfo = imageService.query().eq("image_name", imageName).one();
        if (imageInfo == null) {
            imageInfo = new ImageInfo();
            String uuid = GetIdUtils.getUUID();
            imageInfo.setImageId(uuid);
            Double rank = 2.5;
            imageInfo.setImageName(imageName);
            imageInfo.setImageDesc(imageName);
            imageInfo.setImageVulName(imageName);
            imageInfo.setRank(rank);
        }

        InspectImageResponse image = null;
        try {
            image = DockerTools.getImageByName(imageName);
            if(image == null){
                // pull image from dockerhub
                DockerTools.pullImageByName(imageName);
                //todo:拉去镜像的进度条实现
            }
            image = DockerTools.getImageByName(imageName);
            // image == null 说明拉取镜像失败
            if(image == null){
                throw ImagePullFailedException;
            }
        } catch (Exception e) {
            imageInfo.setOk(false);
            imageService.save(imageInfo);
        }
/*            try {

            }
                    try:
            last_info = {}
            progress_info = {
                "total": 0,
                "progress_count": 0,
                "progress": round(0.0, 2),
            }
            black_list = ["total", "progress_count", "progress"]
            for line in api_docker_client.pull(image_name, stream=True, decode=True):
                if "status" in line and "progressDetail" in line and "id" in line:
                    id = line["id"]
                    status = line["status"]
                    if len(line["progressDetail"]) > 0:
                        try:
                            current = line["progressDetail"]["current"]
                            total = line["progressDetail"]["total"]
                            line["progress"] = round((current / total) * 100, 2)
                            if (current / total) > 1:
                                line["progress"] = round(0.99 * 100, 2)
                        except:
                            line["progress"] = round(1 * 100, 2)
                    else:
                        if (("Download" in status or "Pull" in status) and ("complete" in status)) or ("Verifying" in status) or \
                                ("Layer" in status and "already" in status and "exists" in status):
                            line["progress"] = round(100.00, 2)
                        else:
                            line["progress"] = round(0.00, 2)
                    progress_info[id] = line
                    progress_info["total"] = len(progress_info) - len(black_list)
                    progress_count = 0
                    for key in progress_info:
                        if key in black_list:
                            continue
                        if 100.00 != progress_info[key]["progress"]:
                            continue
                        progress_count += 1
                    progress_info["progress_count"] = progress_count
                    progress_info["progress"] = round((progress_count/progress_info["total"])*100, 2)
                    r.set(str(task_id), json.dumps(progress_info,ensure_ascii=False))
                    print(json.dumps(progress_info, ensure_ascii=False))
                last_info = line
            if "status" in last_info and ("Downloaded newer image for" in last_info["status"] or "Image is up to date for" in last_info["status"]):
                image = client.images.get(image_name)
            else:
                raise Exception
        except ImageNotFound:
            msg = R.build(msg="%s 不存在")
        except Exception:
            traceback.print_exc()
            msg = R.err(msg="%s 添加失败" % (image_name,))
            */
        Result msg = null;
        if (image != null) {
//            List<String> portList = new ArrayList<>();
            StringBuffer imagePort = new StringBuffer();
            if (image.getConfig() != null) {
                ExposedPort[] exposedPorts = image.getConfig().getExposedPorts();
                for (int i = 0; i < exposedPorts.length; i++) {
                    if (i != exposedPorts.length - 1) {
                        imagePort.append(exposedPorts[i].getPort()).append(",");
                    } else {
                        imagePort.append(exposedPorts[i].getPort());
                    }
                }
            }
            imageInfo.setImagePort(imagePort.toString());
            imageInfo.setOk(true);
            imageInfo.setCreateDate(LocalDateTime.now());
            imageInfo.setUpdateDate(LocalDateTime.now());
            HashMap<String, Object> portList = new HashMap<>();
            portList.put("image_port", imagePort);
            msg = Result.ok(imageName + "add successfully", JSON.toJSONString(portList));
            LambdaQueryWrapper<ImageInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(true, ImageInfo::getImageName, imageName);
            imageService.saveOrUpdate(imageInfo, wrapper);
            taskInfo.setTaskStatus(3);
        } else {
            taskInfo.setTaskStatus(4);
        }
        taskInfo.setTaskMsg(JSON.toJSONString(msg));
        taskInfo.setUpdateDate(LocalDateTime.now());
        updateById(taskInfo);
//        LambdaQueryWrapper<TaskInfo> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(true, TaskInfo::getTaskId, taskId);
//        update(taskInfo, wrapper);
    }

    public String createCreateImageTask(ImageInfo imageInfo, UserDTO user) {
        String imageName = imageInfo.getImageName();
        if (!"".equals(imageName) && !imageName.contains(":")) {
            imageName = imageName + ":latest";
        }
        Integer userId = user.getId();

        Map<String, String> args = new HashMap<>();
        args.put("image_name", imageName);
        //    task_info = TaskInfo(
        //    task_name="拉取镜像：" + image_name,
        //    user_id=user_id,
        //    task_status=1,
        //    task_msg=json.dumps({}),
        //    ask_start_date=timezone.now(),
        //    operation_type=1,
        //    operation_args=json.dumps(args),
        //    create_date=timezone.now(),
        //    update_date=timezone.now())
        String uuid = IdUtil.simpleUUID();
        // save taskInfo wait consumer
        TaskInfo taskInfo = new TaskInfo();
        taskInfo.setTaskId(uuid);
        taskInfo.setTaskName("拉取镜像：" + imageName);
        taskInfo.setUserId(userId);
        taskInfo.setTaskStatus(1);
        taskInfo.setTaskMsg("");
        taskInfo.setOperationType("1");
        taskInfo.setOperationArgs(JSON.toJSONString(args));
        taskInfo.setCreateDate(LocalDateTime.now());
        taskInfo.setTaskStartDate(LocalDateTime.now());
        taskInfo.setTaskEndDate(null);
        taskInfo.setIsShow(true);
        taskInfo.setUpdateDate(LocalDateTime.now());
        save(taskInfo);
        return uuid;
    }

    private String createBaseContainerTask(ContainerVul containerVul, UserDTO user, int operationType) {
        // 1:拉取镜像 2: 创建/启动 容器 3:停止容器 4:删除容器
        String taskNameBase = "";
        if (operationType == 1) {
            taskNameBase = "拉取镜像";
        } else if (operationType == 2) {
            taskNameBase = "运行容器";
        } else if (operationType == 3) {
            taskNameBase = "停止容器";
        } else {
            taskNameBase = "删除容器";
        }

        String imageId = containerVul.getImageIdId();
        ImageInfo imageInfo = imageService.query().eq("image_id", imageId).one();
        String imageName = imageInfo.getImageName();
        String imagePort = imageInfo.getImagePort();
        Integer userId = user.getId();
        Map<String, Object> args = new HashMap<>();
        args.put("image_Name", imageName);
        args.put("user_id", userId);
        args.put("image_port", imagePort);
        args.put("container_id", containerVul.getContainerId());
//        task_info = TaskInfo(task_name=task_name_base+"：" + image_name, user_id=user_id, task_status=1,
//                task_start_date=timezone.now(), operation_type=operation_type, task_msg=json.dumps({}),
//                operation_args=json.dumps(args), create_date=timezone.now(), update_date=timezone.now())
        TaskInfo taskInfo = new TaskInfo();
        String taskId = IdUtil.simpleUUID();
        taskInfo.setTaskId(taskId);
        taskInfo.setTaskName(taskNameBase + ":" + imageName);
        taskInfo.setUserId(userId);
        taskInfo.setTaskStatus(1);
        taskInfo.setTaskStartDate(LocalDateTime.now());
        taskInfo.setOperationType(String.valueOf(operationType));
        taskInfo.setTaskMsg("");
        taskInfo.setOperationArgs(JSON.toJSONString(args));
        taskInfo.setCreateDate(LocalDateTime.now());
        taskInfo.setUpdateDate(LocalDateTime.now());
        save(taskInfo);
        return taskId;
    }

    //    @Async
    public void getStatus() {
        try {
            Thread.sleep(2000);
            System.out.println("<发送了一份邮件给用户>");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

package com.sunzy.vulfocus.service.impl;

import cn.hutool.core.lang.hash.Hash;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.api.model.InternetProtocol;
import com.sunzy.vulfocus.common.Result;
import com.sunzy.vulfocus.model.dto.ImageDTO;
import com.sunzy.vulfocus.model.dto.UserDTO;
import com.sunzy.vulfocus.model.po.ImageInfo;
import com.sunzy.vulfocus.model.po.TaskInfo;
import com.sunzy.vulfocus.mapper.TaskInfoMapper;
import com.sunzy.vulfocus.service.ImageInfoService;
import com.sunzy.vulfocus.service.SysLogService;
import com.sunzy.vulfocus.service.TaskInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunzy.vulfocus.utils.DockerTools;
import com.sunzy.vulfocus.utils.GetIpUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author sunzy
 * @since 2023-04-01
 */
@Service
public class TaskInfoServiceImpl extends ServiceImpl<TaskInfoMapper, TaskInfo> implements TaskInfoService {

    @Resource
    private SysLogService logService;


    @Resource
    private ImageInfoService imageService;

    @Override
    public String createImageTask(ImageInfo imageInfo, UserDTO user) {
        Integer userId = user.getId();
        String taskId = createCreateImageTask(imageInfo, user);
        TaskInfo taskInfo = new TaskInfo();
        String imageName = imageInfo.getImageName();
        String imageVulName = imageInfo.getImageVulName();
        String imageDesc = imageInfo.getImageDesc();
        Double rank = imageInfo.getRank();

        if(user.getSuperuser()){
            taskInfo = query().eq("task_id", taskId).one();
            // TODO create image by file
            // if(file != null){
            // ....

            if(!StrUtil.isBlank(imageName)){
                //
                createImage(taskId);
            }
            // log
            logService.sysImageLog(user, imageInfo, "创建");

        } else {

            taskInfo = query().eq("task_id", taskId).one();
            Result msg = Result.fail("权限不足");
            taskInfo.setTaskMsg(JSON.toJSONString(msg));
            taskInfo.setTaskStatus(3);
            taskInfo.setUpdateDate(LocalDateTime.now());
            LambdaQueryWrapper<TaskInfo> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(true, TaskInfo::getTaskId, taskId);
            update(taskInfo, wrapper);
        }
        return taskId;
    }

    @Transactional
    void createImage(String taskId) {
        System.out.println("create image ...");
        TaskInfo taskInfo = query().eq("task_id", taskId).one();
        if(taskInfo == null){
            return;
        }
        String operationArgs = taskInfo.getOperationArgs();
        Map args = JSON.parseObject(operationArgs, Map.class);
        String imageName =(String) args.get("image_name");

//        ImageDTO imageDTO = JSON.parseObject(operationArgs, Map.class);
//        ImageDTO imageDTO = new ImageDTO();
//        String imageName = imageDTO.getImageName();
        ImageInfo imageInfo = imageService.query().eq("image_name", imageName).one();
        if(imageInfo == null){
            imageInfo = new ImageInfo();
            String uuid = GetIpUtils.getUUID();
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
        } catch (Exception e){
            imageInfo.setOk(false);
            imageService.save(imageInfo);
            // pull image from dockerhub
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
        if(image != null){
//            List<String> portList = new ArrayList<>();
            StringBuffer imagePort = new StringBuffer();
            if(image.getConfig() != null){
                ExposedPort[] exposedPorts = image.getConfig().getExposedPorts();
                for (int i = 0; i < exposedPorts.length; i++) {
                    if(i != exposedPorts.length - 1){
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
        LambdaQueryWrapper<TaskInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(true, TaskInfo::getTaskId, taskId);
        update(taskInfo, wrapper);
    }


    public String createCreateImageTask(ImageInfo imageInfo, UserDTO user){
        String imageName = imageInfo.getImageName();
        if(!"".equals(imageName) && !imageName.contains(":")){
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
}

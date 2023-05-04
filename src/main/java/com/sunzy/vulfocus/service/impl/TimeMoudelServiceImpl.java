package com.sunzy.vulfocus.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sunzy.vulfocus.common.Result;
import com.sunzy.vulfocus.model.dto.TimeMoudelDTO;
import com.sunzy.vulfocus.model.dto.TimeTempDTO;
import com.sunzy.vulfocus.model.dto.UserDTO;
import com.sunzy.vulfocus.model.po.ContainerVul;
import com.sunzy.vulfocus.model.po.TimeMoudel;
import com.sunzy.vulfocus.mapper.TimeMoudelMapper;
import com.sunzy.vulfocus.model.po.TimeRank;
import com.sunzy.vulfocus.model.po.TimeTemp;
import com.sunzy.vulfocus.service.TimeMoudelService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunzy.vulfocus.utils.UserHolder;
import com.sunzy.vulfocus.utils.Utils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author sunzy
 * @since 2023-05-04
 */
@Service
public class TimeMoudelServiceImpl extends ServiceImpl<TimeMoudelMapper, TimeMoudel> implements TimeMoudelService {

    @Resource
    private TimeTempServiceImpl timeTempService;

    @Resource
    private TimeRankServiceImpl timeRankService;

    @Resource
    private ContainerVulServiceImpl containerVulService;

    @Resource
    private TaskInfoServiceImpl taskService;

    @Resource
    private ImageInfoServiceImpl imageInfoService;

    @Resource
    private SysLogServiceImpl logService;


    @Override
    public Result get() {
        UserDTO user = UserHolder.getUser();
        Integer userId = user.getId();
        List<TimeMoudel> timeMoudelList = query().eq("user_id", userId).list();
        ArrayList<TimeMoudelDTO> timeMoudelDTOS = new ArrayList<TimeMoudelDTO>();
        for (TimeMoudel timeMoudel : timeMoudelList) {
            TimeMoudelDTO timeMoudelDTO = handleTimeMoudel(timeMoudel);
            timeMoudelDTOS.add(timeMoudelDTO);
        }
        return Result.ok(timeMoudelDTOS);
    }

    /**
     * 删除时间模式，会删除所有改用户运行的容器
     * @return
     */
    @Override
    public Result delete() {
        UserDTO user = UserHolder.getUser();
        long nowTime = Utils.dataTimeToTimestamp(LocalDateTime.now());
        Integer userId = user.getId();
        LambdaQueryWrapper<TimeMoudel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(true, TimeMoudel::getUserId, userId);
        queryWrapper.le(true, TimeMoudel::getEndTime, nowTime);
        TimeMoudel timeMoudel = getOne(queryWrapper);
        List<ContainerVul> containerVulList = null;
        try {
            if(timeMoudel != null){
                String timeId = timeMoudel.getTimeId();
                LambdaQueryWrapper<ContainerVul> deleteContainerWrapper = new LambdaQueryWrapper<>();
                deleteContainerWrapper.eq(true, ContainerVul::getUserId, userId);
                deleteContainerWrapper.eq(true, ContainerVul::getTimeModelId, timeId);
                containerVulList = containerVulService.list(deleteContainerWrapper);
            } else {
                LambdaQueryWrapper<TimeMoudel> timeMoudelQuery = new LambdaQueryWrapper<>();
                timeMoudelQuery.eq(true, TimeMoudel::getUserId, userId);
                timeMoudelQuery.ge(true, TimeMoudel::getEndTime, nowTime);
                timeMoudel = getOne(timeMoudelQuery);
                if(timeMoudel == null){
                    return Result.ok();
                }
                String timeId = timeMoudel.getTimeId();
                LambdaQueryWrapper<ContainerVul> deleteContainerWrapper = new LambdaQueryWrapper<>();
                deleteContainerWrapper.eq(true, ContainerVul::getUserId, userId);
                deleteContainerWrapper.eq(true, ContainerVul::getTimeModelId, timeId);
                containerVulList = containerVulService.list(deleteContainerWrapper);
            }

            for (ContainerVul containerVul : containerVulList) {
                try {
                    taskService.deleteContainerTask(containerVul, user);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return new Result(201, "成功", "{code: 2000");
        } catch (Exception e){
            e.printStackTrace();
            return Result.timeFailed(e.toString());
        }
    }

    @Override
    public Result info() {
        UserDTO user = UserHolder.getUser();
        long nowTime = Utils.dataTimeToTimestamp(LocalDateTime.now());
        Integer userId = user.getId();
        LambdaQueryWrapper<TimeMoudel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(true, TimeMoudel::getUserId, userId);
        queryWrapper.ge(true, TimeMoudel::getEndTime, nowTime);
        TimeMoudel timeMoudel = getOne(queryWrapper);
        if(timeMoudel == null){
            return Result.timeFailed("不在答题模式中");
        }
        TimeMoudelDTO timeMoudelDTO = handleTimeMoudel(timeMoudel);
        String timeId = timeMoudelDTO.getTimeId();
        double totalRank = 0.0;
        LambdaQueryWrapper<ContainerVul> containerWrapper = new LambdaQueryWrapper<>();
        containerWrapper.eq(true, ContainerVul::getUserId, userId);
        containerWrapper.eq(true, ContainerVul::getIScheck, true);
        List<ContainerVul> containerVulList = containerVulService.list(containerWrapper);
        HashMap<String, Double> imageRankCache = new HashMap<>();
        for (ContainerVul containerVul : containerVulList) {
            String imageId = containerVul.getImageIdId();
            if(imageRankCache.containsKey(imageId)){
                totalRank += imageRankCache.get(imageId);
            } else {
                Double rank = imageInfoService.getById(imageId).getRank();
                imageRankCache.put(imageId, rank);
                totalRank += rank;
            }
        }
        LambdaQueryWrapper<TimeRank> timeRankWrapper = new LambdaQueryWrapper<>();
        timeRankWrapper.eq(true, TimeRank::getTimeTempId, timeMoudel.getTempTimeIdId());
        timeRankWrapper.eq(true, TimeRank::getUserId, userId);
        TimeRank timeRank = timeRankService.getOne(timeRankWrapper);
        if(timeRank != null) {
            timeRank.setRank(totalRank);
            timeRankService.updateById(timeRank);
        } else {
            timeRank = new TimeRank();
            timeRank.setRankId(Utils.getUUID());
            timeRank.setRank(totalRank);
            timeRank.setTimeTempId(timeMoudel.getTempTimeIdId());
            timeRank.setUserId(userId);
            timeRank.setUserName(user.getUsername());
            timeRankService.save(timeRank);
        }
        timeMoudelDTO.setRank(totalRank);

        return Result.ok(timeMoudelDTO);
    }

    @Override
    public Result check() {
        UserDTO user = UserHolder.getUser();
        long nowTime = Utils.dataTimeToTimestamp(LocalDateTime.now());
        TimeMoudel data = query().eq("user_id", user.getId()).ge("end_time", nowTime).one();

        if(data != null){
            LambdaQueryWrapper<ContainerVul> deleteContainerWrapper = new LambdaQueryWrapper<>();
            deleteContainerWrapper.eq(true, ContainerVul::getUserId, user.getId());
            deleteContainerWrapper.eq(true, ContainerVul::getTimeModelId, data.getTimeId());
            List<ContainerVul> containerVulList = containerVulService.list(deleteContainerWrapper);
            for (ContainerVul containerVul : containerVulList) {
                try {
                    taskService.deleteContainerTask(containerVul, user);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return Result.ok();
        } else {
            return Result.timeFailed("时间已到");
        }
    }

    @Override
    public Result create(TimeTempDTO timeTempDTO) {
        long nowTime = Utils.dataTimeToTimestamp(LocalDateTime.now());
        UserDTO user = UserHolder.getUser();
        Integer timeRange = timeTempDTO.getTimeRange();
        String tempId = timeTempDTO.getTempId();
        TimeMoudel data = query().eq("user_id", user.getId()).ge("end_time", nowTime).one();
        TimeRank timeRank = timeRankService.query().eq("user_id", user.getId()).eq("time_temp_id", tempId).one();
        if(timeRank == null){
            timeRank = new TimeRank();
            timeRank.setRankId(Utils.getUUID());
            timeRank.setRank(0.0);
            timeRank.setTimeTempId(tempId);
            timeRank.setUserId(user.getId());
            timeRank.setUserName(user.getUsername());
            timeRankService.save(timeRank);
        }
        if(data != null){
            return Result.timeFailed("时间未到");
        } else {
            logService.sysTimeLog(user, "创建");
            TimeMoudel timeMoudel = new TimeMoudel();
            LocalDateTime nowDateTime = LocalDateTime.now();
            LocalDateTime endDateTime = nowDateTime.plusMinutes(timeTempDTO.getTimeRange());
            long startTimestamp = Utils.dataTimeToTimestamp(LocalDateTime.now());
            long endTimeStamp = Utils.dataTimeToTimestamp(endDateTime);
            timeMoudel.setTimeId(Utils.getUUID());
            timeMoudel.setStartTime(startTimestamp);
            timeMoudel.setEndTime(endTimeStamp);
            timeMoudel.setUserId(user.getId());
            timeMoudel.setTempTimeIdId(tempId);
            timeMoudel.setStatus(true);
            save(timeMoudel);
            TimeMoudelDTO timeMoudelDTO = handleTimeMoudel(timeMoudel);
            return Result.ok(timeMoudelDTO);
        }
    }



    private TimeMoudelDTO handleTimeMoudel(TimeMoudel timeMoudel){
        TimeMoudelDTO timeMoudelDTO = new TimeMoudelDTO();
        BeanUtils.copyProperties(timeMoudel, timeMoudelDTO);
        timeMoudelDTO.setEndDate(Utils.timeStampToDatetime(timeMoudel.getEndTime()));
        timeMoudelDTO.setStartDate(Utils.timeStampToDatetime(timeMoudel.getStartTime()));
        return timeMoudelDTO;
    }



}

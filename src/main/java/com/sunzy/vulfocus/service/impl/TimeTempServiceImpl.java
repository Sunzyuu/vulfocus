package com.sunzy.vulfocus.service.impl;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sunzy.vulfocus.common.Result;
import com.sunzy.vulfocus.model.dto.TimeTempDTO;
import com.sunzy.vulfocus.model.dto.UserDTO;
import com.sunzy.vulfocus.model.po.TimeMoudel;
import com.sunzy.vulfocus.model.po.TimeTemp;
import com.sunzy.vulfocus.mapper.TimeTempMapper;
import com.sunzy.vulfocus.service.TimeTempService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunzy.vulfocus.utils.UserHolder;
import com.sunzy.vulfocus.utils.Utils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
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
public class TimeTempServiceImpl extends ServiceImpl<TimeTempMapper, TimeTemp> implements TimeTempService {

    @Resource
    private TimeMoudelServiceImpl timeMoudelService;

    @Override
    public Result createTimeTemp(TimeTempDTO timeTempDTO) {
        UserDTO user = UserHolder.getUser();
        String desc = timeTempDTO.getDesc();
        Integer timeRange = timeTempDTO.getTimeRange();
        if(timeRange == null || timeRange % 30 != 0 ){
            Result result = new Result();
            result.setStatus(2001);
            result.setMsg("时间范围不能为空，必须是整数，并且是30的倍数");
            return result;
        }
        TimeTemp timeTemp = query().eq("time_range", timeRange).one();
        if(timeTemp != null){
            return Result.timeFailed("该时间模式已经创建");
        }
        timeTemp = new TimeTemp();
        String imageName = timeTempDTO.getImageName();
        timeTemp.setTempId(Utils.getUUID());
        timeTemp.setTimeDesc(desc);
        timeTemp.setUserId(user.getId());
        timeTemp.setFlagStatus(false);
        timeTemp.setImageName(imageName);
        timeTemp.setTimeRange(timeRange);
        timeTemp.setTimeImgType(".jpg");
        timeTemp.setRankRange("");
        save(timeTemp);
        return Result.ok( "", JSON.toJSONString(timeTemp));
    }

    @Override
    public Result deleteTimeTemp(String timeTempId) {
        if(StrUtil.isBlank(timeTempId)){
            return Result.fail("id不能为空");
        }

        UserDTO user = UserHolder.getUser();
        if(!user.getSuperuser()){
            return Result.build("权限不足", null);
        }
        LambdaQueryWrapper<TimeMoudel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(true, TimeMoudel::getTempTimeIdId, timeTempId);
        queryWrapper.gt(true, TimeMoudel::getEndTime, Utils.dataTimeToTimestamp(LocalDateTime.now()));
        List<TimeMoudel> list = timeMoudelService.list(queryWrapper);
        if(list != null && list.size() > 0){
            return Result.timeFailed("删除失败，该模版计时模式已启动");
        }

        try {
            removeById(timeTempId);
        } catch (Exception e){
            e.printStackTrace();
            return Result.timeFailed("删除失败");
        }
        return Result.ok("删除成功");
    }

    @Override
    public Result getTimeTemp() {
        UserDTO user = UserHolder.getUser();
        if(!user.getSuperuser()){
            return Result.fail("权限不足");
        }
        List<TimeTemp> timeTempList = list();
        return Result.ok(timeTempList);
    }
}

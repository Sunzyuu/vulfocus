package com.sunzy.vulfocus.service.impl;

import com.sunzy.vulfocus.common.Result;
import com.sunzy.vulfocus.model.po.TimeMoudel;
import com.sunzy.vulfocus.model.po.TimeRank;
import com.sunzy.vulfocus.mapper.TimeRankMapper;
import com.sunzy.vulfocus.model.po.TimeTemp;
import com.sunzy.vulfocus.service.TimeRankService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
public class TimeRankServiceImpl extends ServiceImpl<TimeRankMapper, TimeRank> implements TimeRankService {

    @Resource
    private TimeMoudelServiceImpl timeMoudelService;

    @Resource
    private TimeTempServiceImpl timeTempService;
    @Override
    public Result getRank(Integer timeRange) {
        if (timeRange == null || timeRange % 30 != 0){
            return Result.fail("参数不合法");
        }
        TimeTemp timeTemp = timeTempService.query().eq("time_range", timeRange).one();
        if(timeTemp == null){
            return Result.fail("模式不存在");
        }
        List<TimeRank> timeRanks = query().eq("time_temp_id", timeTemp.getTempId()).list();

        return Result.ok(timeRanks);
    }
}

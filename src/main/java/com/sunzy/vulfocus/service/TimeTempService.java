package com.sunzy.vulfocus.service;

import com.sunzy.vulfocus.common.Result;
import com.sunzy.vulfocus.model.dto.TimeTempDTO;
import com.sunzy.vulfocus.model.po.TimeTemp;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author sunzy
 * @since 2023-05-04
 */
public interface TimeTempService extends IService<TimeTemp> {
    Result createTimeTemp(TimeTempDTO timeTempDTO);

    Result deleteTimeTemp(String timeTempId);

    Result getTimeTemp();
}

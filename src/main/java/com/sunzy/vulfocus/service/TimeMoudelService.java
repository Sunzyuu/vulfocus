package com.sunzy.vulfocus.service;

import com.sunzy.vulfocus.common.Result;
import com.sunzy.vulfocus.model.dto.TimeMoudelDTO;
import com.sunzy.vulfocus.model.dto.TimeTempDTO;
import com.sunzy.vulfocus.model.po.TimeMoudel;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author sunzy
 * @since 2023-05-04
 */
public interface TimeMoudelService extends IService<TimeMoudel> {
    Result get();

    Result delete();

    Result info();

    Result check();

    Result create(TimeTempDTO timeTempDTO);
}

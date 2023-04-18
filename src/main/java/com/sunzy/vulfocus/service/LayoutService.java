package com.sunzy.vulfocus.service;

import com.sunzy.vulfocus.common.Result;
import com.sunzy.vulfocus.model.dto.LayoutDTO;
import com.sunzy.vulfocus.model.po.Layout;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author sunzy
 * @since 2023-04-17
 */
public interface LayoutService extends IService<Layout> {

    public Result CreateLayout(LayoutDTO layoutDTO);
}

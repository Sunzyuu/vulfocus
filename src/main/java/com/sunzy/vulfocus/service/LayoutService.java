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

    Result CreateLayout(LayoutDTO layoutDTO);

    Result runLayout(String layoutId);

    Result stopLayout(String layoutId);

    Result releaseLayout(String layoutId);

    Result deleteLayout(String layoutId);

    Result flagLayout(String layoutId, String flag);

    Result getLayout(String layoutId);

    Result getLayoutList(String query, int page, String flag);

    Result getLayoutRank(String layoutId, int page);
}

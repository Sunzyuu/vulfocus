package com.sunzy.vulfocus.service;

import com.sunzy.vulfocus.common.Result;
import com.sunzy.vulfocus.model.dto.NetworkDTO;
import com.sunzy.vulfocus.model.po.NetWorkInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author sunzy
 * @since 2023-04-14
 */
public interface NetWorkInfoService extends IService<NetWorkInfo> {

    Result createNetWorkInfo(NetworkDTO networkDTO);

    Result removeNetWorkInfo(String networkId);

    Result getNetWorkInfoList(String data, int page);
}

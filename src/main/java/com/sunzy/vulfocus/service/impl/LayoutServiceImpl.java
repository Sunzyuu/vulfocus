package com.sunzy.vulfocus.service.impl;

import cn.hutool.core.util.StrUtil;
import com.sunzy.vulfocus.common.Result;
import com.sunzy.vulfocus.model.dto.LayoutDTO;
import com.sunzy.vulfocus.model.dto.UserDTO;
import com.sunzy.vulfocus.model.po.Layout;
import com.sunzy.vulfocus.mapper.LayoutMapper;
import com.sunzy.vulfocus.service.LayoutService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunzy.vulfocus.utils.UserHolder;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author sunzy
 * @since 2023-04-17
 */
@Service
public class LayoutServiceImpl extends ServiceImpl<LayoutMapper, Layout> implements LayoutService {

    @Override
    public Result CreateLayout(LayoutDTO layoutDTO) {
        UserDTO user = UserHolder.getUser();
        if(!user.getSuperuser()){
            return Result.fail("权限不足");
        }
        Object data = layoutDTO.getData();

        String name = layoutDTO.getName();
        String desc = layoutDTO.getDesc();
        if(StrUtil.isBlank(name)){
            return Result.fail("名称不能为空");
        }
        if(data == null){
            return Result.fail("参数不能为空");
        }

        return null;
    }


}

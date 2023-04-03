package com.sunzy.vulfocus.service;

import com.sunzy.vulfocus.common.Result;
import com.sunzy.vulfocus.model.dto.UserDTO;
import com.sunzy.vulfocus.model.po.UserUserprofile;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author sunzy
 * @since 2023-04-01
 */
public interface UserUserprofileService extends IService<UserUserprofile> {

    public Result register(UserDTO userDTO);

    public Result login(UserDTO userDTO);

    public Result logout();

    public Result getAllUser(int page);

    public Result getUserInfo();
}

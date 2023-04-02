package com.sunzy.vulfocus.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.sunzy.vulfocus.common.Result;
import com.sunzy.vulfocus.common.SystemConstants;
import com.sunzy.vulfocus.model.dto.UserDTO;
import com.sunzy.vulfocus.model.po.UserUserprofile;
import com.sunzy.vulfocus.mapper.UserUserprofileMapper;
import com.sunzy.vulfocus.service.UserUserprofileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunzy.vulfocus.utils.JwtUtil;
import com.sunzy.vulfocus.utils.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author sunzy
 * @since 2023-04-01
 */
@Service
public class UserUserprofileServiceImpl extends ServiceImpl<UserUserprofileMapper, UserUserprofile> implements UserUserprofileService {

    @Override
    public Result register(UserDTO userDTO) {
        String username = userDTO.getName();
        String email = userDTO.getEmail();
        String password = userDTO.getPass();
        String password2 = userDTO.getCheckPass();

        if("".equals(username) || "".equals(email) || "".equals(password) || "".equals(password2)){
            return Result.fail("Params is invalid!");
        }
        if(!password.equals(password2)){
            return Result.fail("Password is not same!");
        }
        Pattern pattern= Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}");//\w表示a-z，A-Z，0-9(\\转义符)
        Matcher matcher=pattern.matcher(email);
        boolean isvalid = matcher.matches();
        if(!isvalid){
            return Result.fail("Email is invalid!");
        }
        LambdaQueryWrapper<UserUserprofile> wrapperName = new LambdaQueryWrapper<>();
        wrapperName.eq(true, UserUserprofile::getUsername, username);
        UserUserprofile one = getOne(wrapperName);
        if(one != null){
            return Result.fail("Username has been used!");
        }

        LambdaQueryWrapper<UserUserprofile> wrapperEmail = new LambdaQueryWrapper<>();
        wrapperEmail.eq(true, UserUserprofile::getEmail, email);
        one = getOne(wrapperEmail);
        if(one != null){
            return Result.fail("Email has been used!");
        }

        UserUserprofile user = new UserUserprofile();
        user.setUsername(username);
        user.setPassword(PasswordEncoder.encode(password));
        user.setRole("注册用户");
        user.setEmail(email);
        user.setLastLogin(null);
        user.setActive(true);
        user.setSuperuser(false);
        user.setAvatar(SystemConstants.USER_AVATAR);
        user.setDateJoined(LocalDateTime.now());
        user.setFirstName("");
        user.setLastName("");
        user.setStaff(false);

        boolean isSuccess = save(user);
        if(isSuccess){
            return Result.ok("Register success!");
        } else {
            return Result.fail("Register failed!");
        }

        //{username: "sss", email: "ss@qq.com"}
    }

    @Override
    public Result login(UserDTO userDTO) {
        String username = userDTO.getName();
        String password = userDTO.getPass();

        if("".equals(username) || "".equals(password)){
            return Result.fail("Params is invalid!");
        }
        LambdaQueryWrapper<UserUserprofile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(true, UserUserprofile::getUsername, username);
        UserUserprofile user = getOne(wrapper);
        if(user == null){
            return Result.fail("User not exist!");
        }

        String encodePassword = user.getPassword();
        Boolean isMatch = PasswordEncoder.matches(encodePassword, password);
        if(!isMatch){
            return Result.fail("Password is incorrect!");
        }
        String token = JwtUtil.createToken(user);

        return Result.ok(token);
    }

    @Override
    public Result logout() {
        return Result.ok("ok");
    }
}

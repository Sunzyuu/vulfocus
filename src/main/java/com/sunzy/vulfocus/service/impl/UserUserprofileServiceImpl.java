package com.sunzy.vulfocus.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sunzy.vulfocus.common.Result;
import com.sunzy.vulfocus.common.SystemConstants;
import com.sunzy.vulfocus.model.dto.UserDTO;
import com.sunzy.vulfocus.model.dto.UserInfo;
import com.sunzy.vulfocus.model.po.ContainerVul;
import com.sunzy.vulfocus.model.po.UserUserprofile;
import com.sunzy.vulfocus.mapper.UserUserprofileMapper;
import com.sunzy.vulfocus.service.ContainerVulService;
import com.sunzy.vulfocus.service.ImageInfoService;
import com.sunzy.vulfocus.service.UserUserprofileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunzy.vulfocus.utils.JwtUtil;
import com.sunzy.vulfocus.utils.PasswordEncoder;
import com.sunzy.vulfocus.utils.UserHolder;
import com.sunzy.vulfocus.utils.Utils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author sunzy
 * @since 2023-04-01
 */
@Service
@Transactional
public class UserUserprofileServiceImpl extends ServiceImpl<UserUserprofileMapper, UserUserprofile> implements UserUserprofileService {

    @Resource
    private ContainerVulService containerService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ImageInfoService imageService;

    @Override
    public Result register(UserDTO userDTO) {
        String username = userDTO.getUsername();
        String email = userDTO.getEmail();
        String password = userDTO.getPassword();
//        String password2 = userDTO.getCheckPass();

        if ("".equals(username) || "".equals(email) || "".equals(password)) {
            return Result.fail("Params is invalid!");
        }
//        if(!password.equals(password2)){
//            return Result.fail("Password is not same!");
//        }
        Pattern pattern = Pattern.compile("\\w+@(\\w+.)+[a-z]{2,3}");//\w表示a-z，A-Z，0-9(\\转义符)
        Matcher matcher = pattern.matcher(email);
        boolean isvalid = matcher.matches();
        if (!isvalid) {
            return Result.fail("Email is invalid!");
        }
        LambdaQueryWrapper<UserUserprofile> wrapperName = new LambdaQueryWrapper<>();
        wrapperName.eq(true, UserUserprofile::getUsername, username);
        UserUserprofile one = getOne(wrapperName);
        if (one != null) {
            return Result.fail("Username has been used!");
        }

        LambdaQueryWrapper<UserUserprofile> wrapperEmail = new LambdaQueryWrapper<>();
        wrapperEmail.eq(true, UserUserprofile::getEmail, email);
        one = getOne(wrapperEmail);
        if (one != null) {
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
        if (isSuccess) {
            return Result.ok("Register success!");
        } else {
            return Result.fail("Register failed!");
        }

        //{username: "sss", email: "ss@qq.com"}
    }

    @Override
    public Result login(UserDTO userDTO) {
        String username = userDTO.getUsername();
        String password = userDTO.getPassword();

        if ("".equals(username) || "".equals(password)) {
            return Result.fail("Params is invalid!");
        }
        LambdaQueryWrapper<UserUserprofile> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(true, UserUserprofile::getUsername, username);
        UserUserprofile user = getOne(wrapper);
        if (user == null) {
            return Result.fail("User not exist!");
        }

        String encodePassword = user.getPassword();
        Boolean isMatch = PasswordEncoder.matches(encodePassword, password);
        if (!isMatch) {
            return Result.fail("Password is incorrect!");
        }
        String token = JwtUtil.createToken(user);
        String md5Token = Utils.md5(token);
        stringRedisTemplate.opsForValue().set(SystemConstants.REDIS_USER_TOKEN_PREFIX + user.getId(), md5Token, SystemConstants.JWT_TOKEN_EXPIRATION, TimeUnit.SECONDS);
        return Result.ok("", token);
    }

    @Override
    public Result logout() {
        return Result.ok("ok");
    }

    @Override
    public Result getAllUser(int currentPage) {
        UserDTO user = UserHolder.getUser();
        System.out.println(user.getId());
        if (!user.getSuperuser()) {
            return Result.fail("权限不足！");
        }
//        LambdaQueryWrapper<UserUserprofile> queryWrapper = new LambdaQueryWrapper<>();
        Page<UserUserprofile> userprofilePage = new Page<>(currentPage, SystemConstants.PAGE_SIZE);
        page(userprofilePage);
        List<UserUserprofile> allUser = userprofilePage.getRecords();
        ArrayList<UserInfo> userInfos = new ArrayList<>();
        for (UserUserprofile userprofile : allUser) {
            userInfos.add(handleUserInfo(userprofile));
        }

        Page<UserInfo> userInfoPage = new Page<>();
        BeanUtil.copyProperties(userprofilePage, userInfoPage);
        userInfos.sort(new Comparator<UserInfo>() {
            @Override
            public int compare(UserInfo u1, UserInfo u2) {
                double diff = u2.getRank() - u1.getRank();
                if (diff > 0) {
                    return 1;
                } else if (diff < 0) {
                    return -1;
                }
                return 0;
            }
        });
//        userInfos.sort(Comparator.comparing(UserInfo::getRank));
        userInfoPage.setRecords(userInfos);
        return Result.ok(userInfoPage);
    }

    /**
     * 　    @Override
     * 　　　　    public int compare(UserInfo u1, UserInfo u2) {
     * 　　　　　　    int diff = (int) (u1.getRank() - u2.getRank());
     * 　　　　　　    if (diff > 0) {
     * 　　　　　　　　    return 1;
     * 　　　　　　    }else if (diff < 0) {
     * 　　　　　　　　    return -1;
     * 　　　　　　    }
     * 　　　　　　    return 0; //相等为0
     * 　　　　    }
     * 　　    });
     *
     * @return
     */
    @Override
    public UserInfo getUserInfo() {
        UserDTO user = UserHolder.getUser();
        UserUserprofile userprofile = getById(user.getId());
        UserInfo userInfo = handleUserInfo(userprofile);
        Map<String, Object> res = new HashMap<>();

        return userInfo;
    }

    @Override
    public Result updateUser(UserDTO userDTO) {
        UserDTO user = UserHolder.getUser();
        if (user.getSuperuser() || userDTO.getId().equals(user.getId())) {
            UserUserprofile userprofile = getById(user.getId());
            userprofile.setUsername(userDTO.getUsername());
            userprofile.setPassword(PasswordEncoder.encode(userDTO.getPassword()));
        }
        return Result.fail("权限不足");
    }

    private UserInfo handleUserInfo(UserUserprofile userprofile) {
        UserInfo userInfo = new UserInfo();
        userInfo.setName(userprofile.getUsername());
        userInfo.setId(userprofile.getId());
        userInfo.setAvatar(userprofile.getAvatar());
        userInfo.setEmail(userprofile.getEmail());

        if (userprofile.getSuperuser()) {
            userInfo.setRoles(new String[]{"admin"});
        } else {
            userInfo.setRoles(new String[]{"member"});
        }
        userInfo.setStatusMoudel(0);

        LambdaQueryWrapper<ContainerVul> rankWrapper = new LambdaQueryWrapper<>();
        rankWrapper.eq(true, ContainerVul::getUserId, userprofile.getId());
        rankWrapper.eq(true, ContainerVul::getIScheck, true);
        List<ContainerVul> successfulList = containerService.list(rankWrapper);

        double score = 0.0;
        if (successfulList == null || successfulList.size() == 0) {
            userInfo.setRank(score);
            userInfo.setRank_count(0);
            return userInfo;
        }
        for (ContainerVul containerVul : successfulList) {
            String imageIdId = containerVul.getImageIdId();
            score += imageService.query().eq("image_id", imageIdId).one().getRank();
        }
        userInfo.setRank(score);
        userInfo.setRank_count(successfulList.size());
        return userInfo;
    }
}

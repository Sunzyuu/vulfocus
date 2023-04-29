package com.sunzy.vulfocus.interceptor;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.auth0.jwt.interfaces.Claim;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunzy.vulfocus.common.Result;
import com.sunzy.vulfocus.common.SystemConstants;
import com.sunzy.vulfocus.model.dto.UserDTO;
import com.sunzy.vulfocus.utils.GetRequestIp;
import com.sunzy.vulfocus.utils.JwtUtil;
import com.sunzy.vulfocus.utils.UserHolder;
import com.sunzy.vulfocus.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

    private StringRedisTemplate stringRedisTemplate;


    public LoginInterceptor(StringRedisTemplate stringRedisTemplate){
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equals(request.getMethod())) {
            log.info("通过拦截器");
            return true;
        }

        log.info("进入 interceptor");
        String token = request.getHeader("authorization");
        log.info(token);

        System.out.println(request.getRequestURI());
        Result result = new Result();
        // token为空
        if(StrUtil.isBlank(token)){
            result.setStatus(403);
            result.setData(null);
            result.setMsg("Please login!");
            doResponse(response, result);
            return false;
        }

        Map<String, Claim> userMap = JwtUtil.verifyToken(token);
        //token验证失败
        if(userMap == null){
            result.setStatus(403);
            result.setData(null);
            result.setMsg("Token is invalid!");
            doResponse(response, result);
            return false;
        }
        Integer userId = userMap.get("id").asInt();
        // 从redis中获取该用户的token，看是否一致 从而实现单点登录
        String redisToken = stringRedisTemplate.opsForValue().get(SystemConstants.REDIS_USER_TOKEN_PREFIX + userId.toString());
        boolean equals = StrUtil.equals(redisToken, Utils.md5(token));
        if(!equals){
            result.setStatus(403);
            result.setData(null);
            result.setMsg("Token is invalid!");
            doResponse(response, result);
            return false;
        }

        UserDTO user = UserHolder.getUser();
        if(user == null){ // 第一次访问 需要向threalocal中设置user信息
            user = new UserDTO();
            user.setId(userId);
            user.setUsername(userMap.get("username").asString());
            user.setSuperuser(userMap.get("isSuperuser").asBoolean());
            String ipAddr = GetRequestIp.getIpAddr(request);
            user.setRequestIp(ipAddr);
            UserHolder.saveUser(user);
        }
        log.info("通过拦截器");
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        UserHolder.removeUser();
    }


    private void doResponse(HttpServletResponse response, Result result) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();
        String s = new ObjectMapper().writeValueAsString(JSON.toJSONString(result));
        out.print(s);
        out.flush();
        out.close();
    }
}

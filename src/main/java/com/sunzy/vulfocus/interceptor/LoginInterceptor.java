package com.sunzy.vulfocus.interceptor;

import cn.hutool.core.util.StrUtil;
import com.auth0.jwt.interfaces.Claim;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sunzy.vulfocus.common.Result;
import com.sunzy.vulfocus.model.dto.UserDTO;
import com.sunzy.vulfocus.utils.GetRequestIp;
import com.sunzy.vulfocus.utils.JwtUtil;
import com.sunzy.vulfocus.utils.UserHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("进入 interceptor");
        String token = request.getHeader("Authorization");
        log.info(token);
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

        UserDTO user = UserHolder.getUser();
        if(user == null){
            user = new UserDTO();
            user.setId(userMap.get("id").asInt());
            user.setName(userMap.get("username").asString());
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
        String s = new ObjectMapper().writeValueAsString(result);
        out.print(s);
        out.flush();
        out.close();
    }
}

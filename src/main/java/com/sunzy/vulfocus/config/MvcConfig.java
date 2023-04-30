package com.sunzy.vulfocus.config;

import com.sunzy.vulfocus.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor(stringRedisTemplate)).addPathPatterns("/**").excludePathPatterns(
                "/user/login",
                "/user/register/"
        );


    }

//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        //和页面有关的静态目录都放在项目的static目录下
//        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/images");
//
//    }

}

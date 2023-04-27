package com.sunzy.vulfocus.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 一些系统常量
 */
public class SystemConstants {
    public static final String DOCKERFILE_UPLOAD_DIR = "E:\\Sunzh\\java\\vulfocus\\src\\main\\resources\\dockerfile";
    public static final String DOCKER_COMPOSE_DIR = "E:\\Sunzh\\java\\vulfocus\\src\\main\\resources\\docker-compose\\";
    public static final String IMG_UPLOAD_DIR = "E:\\Sunzh\\java\\vulfocus\\src\\main\\resources\\static";
    public static final String DOCKER_COMPOSE_UP_D = "docker-compose up -d";
    public static final String DOCKER_COMPOSE_PS = "docker-compose ps";
    public static final String DOCKER_COMPOSE_STOP = "docker-compose stop";
    public static final String JWT_TOKEN_SECRET = "123456";
    public static final int DEFAULT_PAGE_SIZE = 5;
    public static final int JWT_TOKEN_EXPIRATION = 3600 * 24 * 7;
    public static final List<String> ALLOWED_IMG_SUFFIX = new ArrayList<>(Arrays.asList("jpg", "jpeg", "png"));
    public static final int DOCKER_CONTAINER_TIME = 30;
    public static final int PAGE_SIZE = 20;
    public static final int HTTP_OK = 200;
    public static final int HTTP_ERROR = 500;
    public static final String USER_AVATAR = "http://www.baimaohui.net/home/image/icon-anquan-logo.png";
}

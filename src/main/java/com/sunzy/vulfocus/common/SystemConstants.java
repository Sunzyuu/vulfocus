package com.sunzy.vulfocus.common;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SystemConstants {
    public static final String DOCKERFILE_UPLOAD_DIR = "E:\\Sunzh\\java\\vulfocus\\src\\main\\resources\\dockerfile";
    public static final String IMG_UPLOAD_DIR = "E:\\Sunzh\\java\\vulfocus\\src\\main\\resources\\img";
    public static final String USER_NICK_NAME_PREFIX = "user_";
    public static final int DEFAULT_PAGE_SIZE = 5;
    public static final List<String> ALLOWED_IMG_SUFFIX = new ArrayList<>(Arrays.asList("jpg", "jpeg", "png"));
    public static final int DOCKER_CONTAINER_TIME = 30;
    public static final int PAGE_SIZE = 20;
    public static final int HTTP_OK = 200;
    public static final int HTTP_ERROR = 500;
    public static final String USER_AVATAR = "http://www.baimaohui.net/home/image/icon-anquan-logo.png";
}

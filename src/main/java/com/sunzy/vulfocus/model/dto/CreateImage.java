package com.sunzy.vulfocus.model.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@Data
public class CreateImage {
    private String imageName;
    private String imageVulName;
    private String imageDesc;
    private double rank;
    // TODO 通过文件构建镜像
    private MultipartFile file;
}

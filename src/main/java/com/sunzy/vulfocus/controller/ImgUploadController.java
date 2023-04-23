package com.sunzy.vulfocus.controller;

import com.sunzy.vulfocus.common.Result;
import com.sunzy.vulfocus.service.ImgUploadService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController("/img")
public class ImgUploadController {
    @Resource
    private ImgUploadService imgService;

    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile file){
        return imgService.upload(file);
    }
}

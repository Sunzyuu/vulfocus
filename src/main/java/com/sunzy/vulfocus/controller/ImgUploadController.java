package com.sunzy.vulfocus.controller;

import com.sunzy.vulfocus.common.Result;
import com.sunzy.vulfocus.service.ImgUploadService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

@RestController
@RequestMapping("/img")
@CrossOrigin
public class ImgUploadController {
    @Resource
    private ImgUploadService imgService;

    @PostMapping("/upload/")
    public Result upload(@RequestParam("img") MultipartFile file){
        return imgService.upload(file);
    }
}

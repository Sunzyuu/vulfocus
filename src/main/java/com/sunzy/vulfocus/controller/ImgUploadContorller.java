package com.sunzy.vulfocus.controller;

import com.sunzy.vulfocus.common.Result;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController("/img")
public class ImgUploadContorller {

    @PostMapping("/upload")
    public Result upload(@RequestParam("file") MultipartFile file){


        return Result.ok();
    }
}

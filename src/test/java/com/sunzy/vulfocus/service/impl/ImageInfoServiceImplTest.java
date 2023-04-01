package com.sunzy.vulfocus.service.impl;

import com.sunzy.vulfocus.common.Result;
import com.sunzy.vulfocus.model.po.LocalImage;
import com.sunzy.vulfocus.service.ImageInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

import java.util.ArrayList;
import java.util.List;


@SpringBootTest
class ImageInfoServiceImplTest {
    @Resource
    private ImageInfoService imageInfoService;
    @Test
    void getLocalImages() {
        Result res = imageInfoService.getLocalImages();
        List<LocalImage> data = (ArrayList<LocalImage>) res.getData();
        System.out.println(data);
    }
}
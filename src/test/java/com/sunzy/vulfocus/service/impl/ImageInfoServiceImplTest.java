package com.sunzy.vulfocus.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.sunzy.vulfocus.common.Result;
import com.sunzy.vulfocus.model.po.ImageInfo;
import com.sunzy.vulfocus.model.po.LocalImage;
import com.sunzy.vulfocus.service.ImageInfoService;
import com.sunzy.vulfocus.common.SystemConstants;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

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

    @Test
    void testPageQuery() {
        Page<ImageInfo> page = new Page<>(1, SystemConstants.PAGE_SIZE);
        Page<ImageInfo> page1 = imageInfoService.page(page);

    }
}
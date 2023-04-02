package com.sunzy.vulfocus.controller;


import com.sunzy.vulfocus.common.Result;
import com.sunzy.vulfocus.service.ImageInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author sunzy
 * @since 2023-04-01
 */
@RestController
@RequestMapping("/images")
public class ImageInfoController {
    @Resource
    private ImageInfoService imageInfoService;

    //http://127.0.0.1:8000/images/?query=&page=1&flag=flag  获取镜像列表
    /**
     * count:1
     * next:null
     * previous:null
     * results:
     * [{image_id: "9adc6a56-ddab-4b0b-9fa9-33a0c2efeaa5",…}]
     * 0:
     * {image_id: "9adc6a56-ddab-4b0b-9fa9-33a0c2efeaa5",…}
     */
    @GetMapping()
    public Map<String, Object> getImages(@RequestParam("query") String query,
                                         @RequestParam("page") int page,
                                         @RequestParam("query") String flag){

        return imageInfoService.getImageList(query, page, flag);
    }


    //http://127.0.0.1:8000/images/local/local/  获取本地镜像列表
    //

    @GetMapping("/local")
    public Result getLocalImages(){
        return imageInfoService.getLocalImages();
    }

}

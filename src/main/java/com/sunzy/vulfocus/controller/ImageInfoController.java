package com.sunzy.vulfocus.controller;


import com.sunzy.vulfocus.common.Result;
import com.sunzy.vulfocus.model.dto.CreateImage;
import com.sunzy.vulfocus.model.dto.ImageDTO;
import com.sunzy.vulfocus.service.ImageInfoService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
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
@CrossOrigin
@RequestMapping("/images")
public class ImageInfoController {
    @Resource
    private ImageInfoService imageInfoService;

    @PostMapping
    public Result createImage(CreateImage image, @RequestParam("file") MultipartFile imageFile){
        System.out.println(image.getImageName());
        return  imageInfoService.createImage(image);
    }


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
    public Result getImages(@RequestParam("query") String query,
                                         @RequestParam(value = "page",defaultValue = "1") int page,
                                         @RequestParam("query") String flag) throws Exception {

        return imageInfoService.getImageList(query, page, flag);
    }


    //http://127.0.0.1:8000/images/local/local/  获取本地镜像列表
    //

    @GetMapping("/local")
    public Result getLocalImages(){
        return imageInfoService.getLocalImages();
    }


    @PostMapping("/local/local_add/")
    public Result imageLocalAdd(@RequestParam("image_names") String imageNames){
        return imageInfoService.batchLocalAdd(imageNames);
    }

    @GetMapping("/{id}/delete/")
    public Result deleteImage(@PathVariable("id") String imageId) throws Exception {
        return imageInfoService.deleteImage(imageId);
    }

    @GetMapping("/{id}/start/")
    public Result startContainer(@PathVariable("id") String imageId) throws Exception {
        return imageInfoService.startContainer(imageId);
    }

    @PostMapping("/{id}/edit/")
    public Result editImage(@PathVariable("id") String imageId,
                            @RequestBody ImageDTO imageDTO
                            ){
        return imageInfoService.editImage(imageDTO);
    }

    @GetMapping("/{id}/download/")
    public Result downloadImage(@PathVariable("id") String imageId){
        return Result.ok();
    }


}

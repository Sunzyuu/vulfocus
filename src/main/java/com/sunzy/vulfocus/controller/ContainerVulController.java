package com.sunzy.vulfocus.controller;


import com.sunzy.vulfocus.common.Result;
import com.sunzy.vulfocus.service.ContainerVulService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author sunzy
 * @since 2023-04-01
 */
@RestController
@RequestMapping("/container")
public class ContainerVulController {

    @Resource
    private ContainerVulService containerService;

    //http://127.0.0.1:8000/container/?flag=list&page=1&image_id=  获取容器列表
    @GetMapping("/")
    public Result getContainerList(@RequestParam(value = "flag", defaultValue = "") String flag,
                                   @RequestParam(value = "page", defaultValue = "1") int page,
                                   @RequestParam(value = "image_id", defaultValue = "") String imageId
                                   ){
        return containerService.getContainers(flag,page, imageId);
    }
    // /container/'+id+'/stop/?flag=list',
    @GetMapping("/{id}/stop/")
    public Result stopContainer(@PathVariable("id") String containerId){
        return containerService.stopContainer(containerId);
    }


    @GetMapping("/{id}/delete/")
    public Result deleteContainer(@PathVariable("id") String containerId){
        return containerService.deleteContainer(containerId);
    }

    @GetMapping("/{id}/start/")
    public Result startContainer(@PathVariable("id") String containerId){
        return containerService.startContainer(containerId);
    }


}

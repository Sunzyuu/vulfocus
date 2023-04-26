package com.sunzy.vulfocus.controller;


import com.sunzy.vulfocus.common.Result;
import com.sunzy.vulfocus.model.dto.NetworkDTO;
import com.sunzy.vulfocus.service.NetWorkInfoService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author sunzy
 * @since 2023-04-14
 */
@RestController
@RequestMapping("/network")
public class NetWorkInfoController {

    @Resource
    private NetWorkInfoService networkService;

    @GetMapping
    public Result getNetWorkInfo(@RequestParam("query") String data,
                                 @RequestParam(value = "page", defaultValue = "1") int page){
        return networkService.getNetWorkInfoList(data);
    }

    @PostMapping
    public Result createNetWorkInfo(@RequestBody NetworkDTO networkDTO){
        return networkService.createNetWorkInfo(networkDTO);
    }


    @DeleteMapping("/{id}/")
    public Result deleteNetWorkInfo(@PathVariable("id") String id){
        return networkService.removeNetWorkInfo(id);
    }


}

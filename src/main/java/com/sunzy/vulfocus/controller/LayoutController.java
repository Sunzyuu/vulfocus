package com.sunzy.vulfocus.controller;


import com.alibaba.fastjson.JSON;
import com.sunzy.vulfocus.common.Result;
import com.sunzy.vulfocus.model.dto.LayoutDTO;
import com.sunzy.vulfocus.service.LayoutService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;


/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author sunzy
 * @since 2023-04-17
 */
@RestController
@RequestMapping("/layout")
public class LayoutController {

    @Resource
    private LayoutService layoutService;

//    @PostMapping("/test")
//    public Result test(@RequestBody Map<String, Object> payload){
//        System.out.println(payload);
//        String name =(String) payload.get("name");
//        System.out.println(name);
//
//        JSON.parse((byte[]) payload.get("data"));
////        Map<String, Object> data = (Map<String, Object>)JSON.parseObject((String) payload.get("data"), Map.class);
////        System.out.println(data.get("nodes"));
//        return Result.ok();
//    }

    @PostMapping
    public Result createLayout(@RequestBody LayoutDTO layoutDTO){
        return layoutService.CreateLayout(layoutDTO);
    }

    @GetMapping("/{id}/delete/")
    public Result deleteLayout(@PathVariable("id") String layoutId){
        return layoutService.deleteLayout(layoutId);
    }


    @GetMapping("/{id}/release/")
    public Result releaseLayout(@PathVariable("id") String layoutId){
        return layoutService.releaseLayout(layoutId);
    }

    @GetMapping
    public Result getLayoutList(
            @RequestParam("query") String query,
            @RequestParam("page") int page,
            @RequestParam("flag") String flag
                                ){
        return layoutService.getLayoutList(query, page, flag);
    }

    @GetMapping("/{id}/rank/")
    public Result getRank(@PathVariable("id") String layoutId,
                          @RequestParam("page") int page){
        return layoutService.getLayoutRank(layoutId, page);
    }

    @GetMapping("/{id}/get/")
    public Result getLayout(@PathVariable("id") String layoutId){
        return layoutService.getLayout(layoutId);
    }

    @GetMapping("/{id}/start/")
    public Result startLayout(@PathVariable("id") String layoutId){
        return layoutService.runLayout(layoutId);
    }

    @GetMapping("/{id}/stop/")
    public Result stopLayout(@PathVariable("id") String layoutId){
        return layoutService.stopLayout(layoutId);
    }

    @GetMapping("/{id}/flag/")
    public Result flagLayout(@PathVariable("id") String layoutId,
                             @RequestParam("flag") String flag){
        return layoutService.flagLayout(layoutId, flag);
    }

}

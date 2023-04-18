package com.sunzy.vulfocus.controller;


import com.alibaba.fastjson.JSON;
import com.sunzy.vulfocus.common.Result;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/test")
    public Result test(@RequestBody Map<String, Object> payload){
        System.out.println(payload);
        String name =(String) payload.get("name");
        System.out.println(name);

        JSON.parse((byte[]) payload.get("data"));
//        Map<String, Object> data = (Map<String, Object>)JSON.parseObject((String) payload.get("data"), Map.class);
//        System.out.println(data.get("nodes"));
        return Result.ok();
    }

}

package com.sunzy.vulfocus.controller;


import com.sunzy.vulfocus.common.Result;
import com.sunzy.vulfocus.service.TimeRankService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author sunzy
 * @since 2023-05-04
 */
@RestController
@RequestMapping("/timerank")
@CrossOrigin
public class TimeRankController {
    @Resource
    private TimeRankService timeRankService;

    @GetMapping("/")
    public Result getRanks(@RequestParam("value") Integer timeRange){
        return timeRankService.getRank(timeRange);
    }
}

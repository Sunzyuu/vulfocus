package com.sunzy.vulfocus.controller;


import com.sunzy.vulfocus.common.Result;
import com.sunzy.vulfocus.model.dto.TimeTempDTO;
import com.sunzy.vulfocus.service.TimeMoudelService;
import com.sunzy.vulfocus.service.impl.TimeMoudelServiceImpl;
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
@RequestMapping("/time")
@CrossOrigin
public class TimeMoudelController {
    @Resource
    private TimeMoudelService timeMoudelService;

    @GetMapping("/")
    public Result getTimeMoudelList(){
        return timeMoudelService.get();
    }

    @PostMapping("/")
    public Result startTimeMoudel(@RequestBody TimeTempDTO timeTempDTO){
        return timeMoudelService.create(timeTempDTO);
    }



}

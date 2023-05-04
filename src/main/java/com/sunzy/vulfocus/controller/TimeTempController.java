package com.sunzy.vulfocus.controller;


import com.sunzy.vulfocus.common.Result;
import com.sunzy.vulfocus.model.dto.TimeTempDTO;
import com.sunzy.vulfocus.service.TimeTempService;
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
@RequestMapping("/timetemp")
@CrossOrigin
public class TimeTempController {

    @Resource
    private TimeTempService timeTempService;


    @GetMapping("/")
    public Result getTimeTemp(){
        return timeTempService.getTimeTemp();
    }

    @PostMapping("/")
    public Result createTimeTemp(@RequestBody TimeTempDTO timeTempDTO){
        return timeTempService.createTimeTemp(timeTempDTO);
    }

    @DeleteMapping("/{id}/")
    public Result deleteTimeTemp(@PathVariable("id") String id){
        return timeTempService.deleteTimeTemp(id);
    }

}

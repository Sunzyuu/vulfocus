package com.sunzy.vulfocus.controller;


import com.sunzy.vulfocus.common.Result;
import com.sunzy.vulfocus.service.SysLogService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("/syslog")
public class SysLogController {
    @Resource
    private SysLogService logService;

    @GetMapping
    public Result getLogs(@RequestParam("query") String data,
                          @RequestParam("page") int page){
        return logService.getSysLog(page,data);
    }
}

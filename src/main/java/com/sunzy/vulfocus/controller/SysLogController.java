package com.sunzy.vulfocus.controller;


import com.sunzy.vulfocus.common.Result;
import com.sunzy.vulfocus.service.SysLogService;
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
@CrossOrigin
@RequestMapping("/syslog")
public class SysLogController {
    @Resource
    private SysLogService logService;

    @GetMapping
    public Result getLogs(@RequestParam(value = "query", defaultValue = "") String data,
                          @RequestParam(value = "page", defaultValue = "1") int page){
        return logService.getSysLog(page,data);
    }
}

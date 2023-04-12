package com.sunzy.vulfocus.controller;


import com.sunzy.vulfocus.common.Result;
import com.sunzy.vulfocus.model.dto.UserDTO;
import com.sunzy.vulfocus.service.UserUserprofileService;
import com.sunzy.vulfocus.utils.UserHolder;
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
@RequestMapping("/user")
public class UserUserprofileController {

    @Resource
    private UserUserprofileService userService;

    @PostMapping("/regiser")
    public Result regiser(@RequestBody UserDTO userDTO){
        return userService.register(userDTO);
    }


    @PostMapping("/login")
    public Result login(@RequestBody UserDTO userDTO){
        return userService.login(userDTO);
    }

    @GetMapping("/logout")
    public Result logout(){
        return userService.logout();
    }



    // http://127.0.0.1:8000/user/?page=1
    /**
     * count:2
     * next:null
     * previous:null
     * results:
     * [{id: 1, name: "sunzy", roles: ["member"],…}, {id: 2, name: "admin", roles: ["admin"],…}]
     * 0
     * :
     * {id: 1, name: "sunzy", roles: ["member"],…}
     * 1
     * :
     * {id: 2, name: "admin", roles: ["admin"],…}
     */
    @GetMapping
    public Result getAllUsers(@RequestParam("page") Integer page){
        return userService.getAllUser(page);
    }

    @GetMapping("/info")
    public Result getUserInfo(){
        return userService.getUserInfo();
    }


}

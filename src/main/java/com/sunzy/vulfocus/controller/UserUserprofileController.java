package com.sunzy.vulfocus.controller;


import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author sunzy
 * @since 2023-04-01
 */
@RestController
@RequestMapping("/user-userprofile")
public class UserUserprofileController {


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
}

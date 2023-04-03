package com.sunzy.vulfocus.model.dto;

import lombok.Data;

import java.util.List;

@Data
public class UserInfo {
    private Integer id;
    private String name;
    private String email;
    private String avatar;
    private List<String> roles;
    private Double rank;
    private Integer statusMoudel;
    private Integer rank_count;
}

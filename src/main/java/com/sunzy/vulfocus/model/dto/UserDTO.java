package com.sunzy.vulfocus.model.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Integer id;
    private String username;
    private String password;
    private String checkPass;
    private String email;
    private boolean isSuperuser;
    private String requestIp;
    public boolean getSuperuser() {
        return isSuperuser;
    }
}

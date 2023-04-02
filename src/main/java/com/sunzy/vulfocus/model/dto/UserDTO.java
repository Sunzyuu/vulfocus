package com.sunzy.vulfocus.model.dto;

import lombok.Data;

@Data
public class UserDTO {
    private String name;
    private String pass;
    private String checkPass;
    private String email;
    private boolean isSuperuser;

    public String getSuperuser() {
        return isSuperuser ? "1":"0";
    }
}

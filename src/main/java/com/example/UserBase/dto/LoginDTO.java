package com.example.UserBase.dto;

import io.swagger.annotations.ApiModelProperty;

public class LoginDTO {

    @ApiModelProperty(position = 0)
    private String email;
    @ApiModelProperty(position = 1)
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}


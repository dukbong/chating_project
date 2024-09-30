package com.example.chating.security.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class UserInfo {

    private String username;
    private String password;
    private String role;

    @Builder
    public UserInfo(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }

}

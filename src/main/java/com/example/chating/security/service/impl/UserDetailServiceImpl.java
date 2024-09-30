package com.example.chating.security.service.impl;

import com.example.chating.security.customdetails.CustomUserDetails;
import com.example.chating.security.service.UserDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailService {

    @Override
    public UserDetails loadUserByUsername(String username) {



        return new CustomUserDetails(null);
    }

}

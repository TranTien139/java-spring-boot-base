package com.example.UserBase.service;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UserService {

    protected PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String encodePassword(String password){
        System.out.println(passwordEncoder + "passwordEncoder");
        return passwordEncoder.encode(password);
    }
}

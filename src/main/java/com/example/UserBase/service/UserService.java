package com.example.UserBase.service;

import com.example.UserBase.dto.UserResponseDTO;
import com.example.UserBase.exception.CustomException;
import com.example.UserBase.model.User;
import com.example.UserBase.repository.UserRepository;
import com.example.UserBase.security.JwtTokenProvider;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    protected PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private AuthenticationManager authenticationManager;

    protected ModelMapper modelMapper = new ModelMapper();

    public String encodePassword(String password){
        return passwordEncoder.encode(password);
    }

    public Map<String, Object> signin(String email, String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
            User user = userRepository.findByEmail(email);
            String token = jwtTokenProvider.createToken(email, user.getRoles());
            Map<String, Object> result = new HashMap<>();
            result.put("accessToken", token);
            Object userInfo = modelMapper.map(user, UserResponseDTO.class);
            result.put("user", userInfo);
            return result;
        } catch (AuthenticationException e) {
            throw new CustomException("Invalid email/password supplied", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public Map<String, Object> signup(User user) {
        if (!userRepository.existsByEmail(user.getEmail())) {
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            userRepository.save(user);
            String token = jwtTokenProvider.createToken(user.getEmail(), user.getRoles());
            Map<String, Object> result = new HashMap<>();
            result.put("accessToken", token);
            Object userInfo = modelMapper.map(user, UserResponseDTO.class);
            result.put("user", userInfo);
            return result;
        } else {
            throw new CustomException("Email is already in use", HttpStatus.UNPROCESSABLE_ENTITY);
        }
    }

    public User whoami(HttpServletRequest req) {
        return userRepository.findByEmail(jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(req)));
    }

}

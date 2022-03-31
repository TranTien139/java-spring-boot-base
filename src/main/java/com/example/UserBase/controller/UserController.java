package com.example.UserBase.controller;

import com.example.UserBase.dto.LoginDTO;
import com.example.UserBase.dto.UserDataDTO;
import com.example.UserBase.dto.UserResponseDTO;
import com.example.UserBase.exception.ResourceNotFoundException;
import com.example.UserBase.model.User;
import com.example.UserBase.repository.UserRepository;
import com.example.UserBase.service.Response;
import com.example.UserBase.service.UserService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.modelmapper.ModelMapper;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    protected ModelMapper modelMapper = new ModelMapper();

    @Autowired
    private Response responseData;

    @GetMapping("/")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Map<String, Object> getAllUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
//        int page = Integer.parseInt(req.getParameter("page"));
//        int pageSize = Integer.parseInt(req.getParameter("pageSize"));
        Page<User> pageData = userRepository.findAll(PageRequest.of(page-1, size, Sort.by("id").descending()));
        List<User> users = new  ArrayList<>();
        if(pageData.hasContent()){
            Object result = pageData.getContent();
            return responseData.responseSuccess(result);
        }
        return responseData.responseSuccess(users);
    }

    @GetMapping("/{id}")
    public Map<String, Object> getUserById(@PathVariable(value = "id") Long userId) throws ResourceNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("User not found on: " + userId));
        // ResponseEntity.ok().body(user);
        Object result = modelMapper.map(user, UserResponseDTO.class);
        return responseData.responseSuccess(result);
    }

    @PutMapping("/edit")
    public Map<String, Object> updateUserById(HttpServletRequest req, @RequestBody UserResponseDTO userDetails) throws ResourceNotFoundException{
        User userInfo = userService.whoami(req);
        User user = userRepository.findById(userInfo.getId()).orElseThrow(()->new ResourceNotFoundException("User not found on: " + userInfo.getId()));
        user.setEmail(userDetails.getEmail());
        user.setFullName(userDetails.getFullName());
        final User updatedUser = userRepository.save(user);
        Object result = modelMapper.map(updatedUser, UserResponseDTO.class);
        return responseData.responseSuccess(result);
    }

    @DeleteMapping("/delete")
    public Map<String, Object> deleteUser(HttpServletRequest req) throws Exception{
        User userInfo = userService.whoami(req);
        User user = userRepository.findById(userInfo.getId()).orElseThrow(()-> new ResourceNotFoundException("User not found on: " + userInfo.getId()));
        userRepository.delete(user);
        Object result = modelMapper.map(user, UserResponseDTO.class);
        return responseData.responseSuccess(result);
    }

    @PostMapping("/login")
//    @ApiOperation(value = "${UserController.login}")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 422, message = "Invalid email/password supplied")})
    public Map<String, Object> login(@ApiParam("Login") @RequestBody LoginDTO user) {
        Object result = userService.signin(user.getEmail(), user.getPassword());
        return responseData.responseSuccess(result);
    }

    @PostMapping("/signup")
//    @ApiOperation(value = "${UserController.signup}")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 422, message = "Username is already in use")})
    public Map<String, Object> signup(@ApiParam("Signup User") @RequestBody UserDataDTO user) {
        Object result = userService.signup(modelMapper.map(user, User.class));
        return responseData.responseSuccess(result);
    }

    @GetMapping(value = "/me")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
//    @ApiOperation(value = "${UserController.me}", response = UserResponseDTO.class, authorizations = { @Authorization(value="apiKey") })
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public Map<String, Object> userInfo(HttpServletRequest req) {
        Object result = modelMapper.map(userService.whoami(req), UserResponseDTO.class);
        return responseData.responseSuccess(result);
    }
}

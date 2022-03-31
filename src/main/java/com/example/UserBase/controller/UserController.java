package com.example.UserBase.controller;

import com.example.UserBase.dto.LoginDTO;
import com.example.UserBase.dto.UserDataDTO;
import com.example.UserBase.dto.UserResponseDTO;
import com.example.UserBase.exception.ResourceNotFoundException;
import com.example.UserBase.model.User;
import com.example.UserBase.repository.UserRepository;
import com.example.UserBase.service.UserService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.modelmapper.ModelMapper;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
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

    @GetMapping("/")
    public List<User> getAllUsers(@RequestParam(defaultValue = "1") int page,
                                  @RequestParam(defaultValue = "10") int size) {
        Page<User> pageData = userRepository.findAll(PageRequest.of(page, size, Sort.by("id").descending()));
        List<User> users = new  ArrayList<>();
        if(pageData.hasContent()){
            return pageData.getContent();
        }
        return users;
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable(value = "id") Long userId) throws ResourceNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("User not found on: " + userId));
        return ResponseEntity.ok().body(user);
    }

    @PutMapping("/edit")
    public ResponseEntity<User> updateUserById(HttpServletRequest req, @RequestBody UserResponseDTO userDetails) throws ResourceNotFoundException{
        User userInfo = userService.whoami(req);
        User user = userRepository.findById(userInfo.getId()).orElseThrow(()->new ResourceNotFoundException("User not found on: " + userInfo.getId()));
        user.setEmail(userDetails.getEmail());
        user.setFullName(userDetails.getFullName());
        final User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public Map<String, Boolean> deleteUser(@PathVariable(value = "id") Long userId) throws Exception{
        User user = userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("User not found on: " + userId));
        userRepository.delete(user);
        Map<String, Boolean> response = new  HashMap<>();
        response.put("status", Boolean.TRUE);
        return response;
    }

    @PostMapping("/login")
    @ApiOperation(value = "${UserController.login}")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 422, message = "Invalid email/password supplied")})
    public String login(@ApiParam("Login") @RequestBody LoginDTO user) {
        return userService.signin(user.getEmail(), user.getPassword());
    }

    @PostMapping("/signup")
    @ApiOperation(value = "${UserController.signup}")
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Something went wrong"),
            @ApiResponse(code = 403, message = "Access denied"),
            @ApiResponse(code = 422, message = "Username is already in use")})
    public String signup(@ApiParam("Signup User") @RequestBody UserDataDTO user) {
        return userService.signup(modelMapper.map(user, User.class));
    }

    @GetMapping(value = "/me")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
    @ApiOperation(value = "${UserController.me}", response = UserResponseDTO.class, authorizations = { @Authorization(value="apiKey") })
    @ApiResponses(value = {//
            @ApiResponse(code = 400, message = "Something went wrong"), //
            @ApiResponse(code = 403, message = "Access denied"), //
            @ApiResponse(code = 500, message = "Expired or invalid JWT token")})
    public UserResponseDTO whoami(HttpServletRequest req) {
        return modelMapper.map(userService.whoami(req), UserResponseDTO.class);
    }
}

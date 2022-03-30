package com.example.UserBase.controller;

import com.example.UserBase.dto.UserDataDTO;
import com.example.UserBase.exception.ResourceNotFoundException;
import com.example.UserBase.model.User;
import com.example.UserBase.repository.UserRepository;
import com.example.UserBase.service.UserService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.modelmapper.ModelMapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    protected ModelMapper modelMapper = new ModelMapper();

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<User> getUserById(@PathVariable(value = "id") Long userId) throws ResourceNotFoundException {
        User user = userRepository.findById(userId).orElseThrow(()-> new ResourceNotFoundException("User not found on: " + userId));
        return ResponseEntity.ok().body(user);
    }

    @PostMapping("/user")
    public User createUser(@Validated @RequestBody User user){
        User userCheck = userRepository.findByEmail(user.getEmail());
        if(userCheck != null) return null;
        User newUser = new User();
        newUser.setEmail(user.getEmail());
        newUser.setFullName(user.getFullName());
        String password = userService.encodePassword(user.getPassword());
        newUser.setPassword(password);
        return userRepository.save(newUser);
    }

    @PutMapping("/user/{id}")
    public ResponseEntity<User> updateUserById(@PathVariable(value = "id") Long userId, @Validated @RequestBody User userDetails) throws ResourceNotFoundException{
        User user = userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User not found on: " + userId));
        user.setEmail(userDetails.getEmail());
        user.setFullName(userDetails.getFullName());
        String password = userService.encodePassword(userDetails.getPassword());
        user.setPassword(password);
        final User updatedUser = userRepository.save(user);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/user/{id}")
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
    public String login(
                        @ApiParam("Email") @RequestParam String email,
                        @ApiParam("Password") @RequestParam String password) {
        return userService.signin(email, password);
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
}

package com.blog.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.blog.backend.dto.LoginRequestDTO;
import com.blog.backend.dto.UserRequestDTO;
import com.blog.backend.dto.UserResponseDTO;
import com.blog.backend.entity.User;
import com.blog.backend.service.AuthService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
public class AuthController {

    private final AuthService userService;

    public AuthController(AuthService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRequestDTO dto) {
        UserResponseDTO savedUser = userService.register(dto);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDTO dto) {
        User user = userService.login(dto);
        return ResponseEntity.ok(user);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
}
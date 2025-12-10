package com.blog.backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping
    public ResponseEntity<UserResponseDTO> createUser(@Valid @RequestBody UserRequestDTO dto) {
        UserResponseDTO savedUser = userService.createUser(dto);
        return ResponseEntity.ok(savedUser);
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
}
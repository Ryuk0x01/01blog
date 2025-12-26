package com.blog.backend.controller;

import com.blog.backend.dto.LoginRequestDTO;
import com.blog.backend.dto.RegisterRequestDTO;
import com.blog.backend.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO dto) {
        return ResponseEntity.ok(authService.register(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO dto) {
        String token = authService.login(dto);
        return ResponseEntity.ok(Map.of("token", token));
    }
}

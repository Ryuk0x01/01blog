package com.blog.backend.service;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.blog.backend.config.JwtUtil;
import com.blog.backend.dto.LoginRequestDTO;
import com.blog.backend.dto.UserRequestDTO;
import com.blog.backend.dto.UserResponseDTO;
import com.blog.backend.entity.User;
import com.blog.backend.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bcrypt;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, BCryptPasswordEncoder encoder, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.bcrypt = encoder;
        this.jwtUtil = jwtUtil;
    }

    public UserResponseDTO register(UserRequestDTO dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(bcrypt.encode(dto.getPassword()));
        user.setRole("USER");

        User savedUser = userRepository.save(user);

        UserResponseDTO res = new UserResponseDTO();
        res.setId(savedUser.getId());
        res.setUsername(savedUser.getUsername());
        res.setEmail(savedUser.getEmail());
        res.setRole(savedUser.getRole());

        return res;
    }

    public String login(LoginRequestDTO dto) {

        User user;

        if (dto.getEmail().contains("@")) {
            user = userRepository.findByEmail(dto.getEmail());
        } else {
            user = userRepository.findByUsername(dto.getEmail());
        }

        if (user == null || !bcrypt.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        return jwtUtil.generateToken(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
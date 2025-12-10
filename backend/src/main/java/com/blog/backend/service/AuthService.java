package com.blog.backend.service;

import java.util.List;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.blog.backend.dto.UserRequestDTO;
import com.blog.backend.dto.UserResponseDTO;
import com.blog.backend.entity.User;
import com.blog.backend.repository.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserResponseDTO createUser(UserRequestDTO dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setRole("USER");

        User savedUser = userRepository.save(user);

        UserResponseDTO res = new UserResponseDTO();
        res.setId(savedUser.getId());
        res.setUsername(savedUser.getUsername());
        res.setEmail(savedUser.getEmail());
        res.setRole(savedUser.getRole());

        return res;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
package com.blog.backend.controller;

import com.blog.backend.entity.User;
import com.blog.backend.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchUsers(@RequestParam String query) {
        if (query == null || query.trim().isEmpty()) {
            return ResponseEntity.ok(List.of());
        }

        List<User> users = userRepository.findByUsernameContainingIgnoreCase(query);
        
        // Map to a simple DTO to avoid exposing sensitive data
        List<Map<String, Object>> result = users.stream()
            .map(u -> {
                Map<String, Object> map = new java.util.HashMap<>();
                map.put("id", u.getId());
                map.put("username", u.getUsername());
                return map;
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(result);
    }
}

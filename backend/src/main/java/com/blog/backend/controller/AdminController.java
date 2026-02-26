package com.blog.backend.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blog.backend.service.AdminService;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(Map.of("data", adminService.getAllUsers()));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok(Map.of("status", "success", "message", "User deleted"));
    }

    @PutMapping("/users/{id}/ban")
    public ResponseEntity<?> toggleUserBan(@PathVariable Long id) {
        adminService.toggleUserBan(id);
        return ResponseEntity.ok(Map.of("status", "success", "message", "User ban status toggled"));
    }

    @GetMapping("/posts")
    public ResponseEntity<?> getAllPosts() {
        return ResponseEntity.ok(Map.of("data", adminService.getAllPosts()));
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id) {
        adminService.deletePost(id);
        return ResponseEntity.ok(Map.of("status", "success", "message", "Post deleted"));
    }

    @PutMapping("/posts/{id}/hide")
    public ResponseEntity<?> togglePostHide(@PathVariable Long id) {
        adminService.togglePostHide(id);
        return ResponseEntity.ok(Map.of("status", "success", "message", "Post hide status toggled"));
    }

    @GetMapping("/reports")
    public ResponseEntity<?> getAllReports() {
        return ResponseEntity.ok(Map.of("data", adminService.getAllReports()));
    }

    @DeleteMapping("/reports/{id}")
    public ResponseEntity<?> dismissReport(@PathVariable Long id) {
        adminService.deleteReport(id);
        return ResponseEntity.ok(Map.of("status", "success", "message", "Report dismissed"));
    }

    @GetMapping("/stats")
    public ResponseEntity<?> getStats() {
        return ResponseEntity.ok(adminService.getStats());
    }
}

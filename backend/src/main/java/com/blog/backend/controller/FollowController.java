package com.blog.backend.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blog.backend.service.FollowService;

@RestController
@RequestMapping("/api/users")
public class FollowController {

    private final FollowService followService;

    public FollowController(FollowService followService) {
        this.followService = followService;
    }

    @PostMapping("/{id}/follow")
    public ResponseEntity<?> follow(@PathVariable Long id, Authentication auth) {

        followService.follow(id, auth.getName());
        return ResponseEntity.ok(Map.of("message", "followed"));
    }

    @DeleteMapping("/{id}/unfollow")
    public ResponseEntity<?> unfollow(@PathVariable Long id, Authentication auth) {

        followService.unfollow(id, auth.getName());
        return ResponseEntity.ok(Map.of("message", "unfollowed"));
    }
}

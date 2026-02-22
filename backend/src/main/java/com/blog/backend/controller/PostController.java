package com.blog.backend.controller;

import com.blog.backend.dto.PostRequestDTO;
import com.blog.backend.dto.PostResponseDTO;
import com.blog.backend.service.PostService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // Create Post → USERS/Admin
    @PostMapping(consumes = { "multipart/form-data" })
    public ResponseEntity<?> createPost(@Valid PostRequestDTO dto, Authentication auth) throws Exception {
        String email = (String) auth.getPrincipal();
        PostResponseDTO created = postService.createPost(dto, email);
        return ResponseEntity.ok(Map.of("status", "success", "data", created));
    }

    // Get all posts → public
    @GetMapping
    public ResponseEntity<?> getAllPosts(Authentication auth) {
        String email = (String) auth.getPrincipal();
        List<PostResponseDTO> posts = postService.getAllPosts(email);
        return ResponseEntity.ok(Map.of("status", "success", "data", posts));
    }

    // Get feed posts (from followed users + self)
    @GetMapping("/feed")
    public ResponseEntity<?> getFeedPosts(Authentication auth) {
        String email = (String) auth.getPrincipal();
        List<PostResponseDTO> posts = postService.getFeedPosts(email);
        return ResponseEntity.ok(Map.of("status", "success", "data", posts));
    }

    // Get posts by user
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getPostsByUser(@PathVariable Long userId, Authentication auth) {
        String email = (String) auth.getPrincipal();
        List<PostResponseDTO> posts = postService.getPostsByUser(userId, email);
        return ResponseEntity.ok(Map.of("status", "success", "data", posts));
    }

    // Get single post → public
    @GetMapping("/{id}")
    public ResponseEntity<?> getPost(@PathVariable Long id, Authentication auth) {
        String email = (String) auth.getPrincipal();
        PostResponseDTO post = postService.getPostByIdDTO(id, email);
        return ResponseEntity.ok(Map.of("status", "success", "data", post));
    }

    // Update post only author
    @PutMapping(value = "/{id}", consumes = { "multipart/form-data" })
    public ResponseEntity<?> updatePost(
            @PathVariable Long id,
            @Valid PostRequestDTO dto,
            Authentication auth) throws Exception {
        String email = (String) auth.getPrincipal();
        System.out.println("---------->>>>>>> " + email);
        PostResponseDTO updated = postService.updatePost(id, dto, email);
        return ResponseEntity.ok(Map.of("status", "success", "data", updated));
    }

    // Delete post → author or admin
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id, Authentication auth) {
        String email = (String) auth.getPrincipal();
        String role = auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        postService.deletePost(id, email, role);
        return ResponseEntity.ok(Map.of("status", "success"));
    }
}

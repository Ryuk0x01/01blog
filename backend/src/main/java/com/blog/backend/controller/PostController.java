package com.blog.backend.controller;

import com.blog.backend.dto.PostRequestDTO;
import com.blog.backend.dto.PostResponseDTO;
import com.blog.backend.service.PostService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> createPost(@Valid PostRequestDTO dto, Authentication auth) throws Exception {
        String email = (String) auth.getPrincipal();
        PostResponseDTO created = postService.createPost(dto, email);
        return ResponseEntity.ok(Map.of("status", "success", "data", created));
    }

    // Get all posts → public
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> getAllPosts() {
        List<PostResponseDTO> posts = postService.getAllPosts();
        return ResponseEntity.ok(Map.of("status", "success", "data", posts));
    }

    // Get single post → public
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> getPost(@PathVariable Long id) {
        PostResponseDTO post = postService.getPostByIdDTO(id);
        return ResponseEntity.ok(Map.of("status", "success", "data", post));
    }

    // Update post only author
    @PutMapping(value = "/{id}", consumes = { "multipart/form-data" })
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> updatePost(
            @PathVariable Long id,
            @Valid PostRequestDTO dto,
            Authentication auth) throws Exception {
        String email = (String) auth.getPrincipal();
        PostResponseDTO updated = postService.updatePost(id, dto, email);
        return ResponseEntity.ok(Map.of("status", "success", "data", updated));
    }

    // Delete post → author or admin
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public ResponseEntity<?> deletePost(@PathVariable Long id, Authentication auth) {
        String email = (String) auth.getPrincipal();
        String role = auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        // System.out.println("---------------------------------------------------------->" + role);
        postService.deletePost(id, email, role);
        return ResponseEntity.ok(Map.of("status", "success"));
    }
}

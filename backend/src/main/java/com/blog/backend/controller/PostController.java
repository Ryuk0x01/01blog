package com.blog.backend.controller;

import com.blog.backend.dto.PostResponseDTO;
import com.blog.backend.entity.Post;
import com.blog.backend.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    // Create Post → USER/Admin
    @PostMapping
    public ResponseEntity<PostResponseDTO> createPost(@RequestBody Post post, Authentication auth) {
        String email = (String) auth.getPrincipal();
        PostResponseDTO created = postService.createPost(post.getTitle(), post.getContent(), email);
        return ResponseEntity.ok(created);
    }

    // Get all posts → public
    @GetMapping
    public ResponseEntity<List<PostResponseDTO>> getAllPosts() {
        return ResponseEntity.ok(postService.getAllPosts());
    }

    // Get single post → public
    @GetMapping("/{id}")
    public ResponseEntity<PostResponseDTO> getPost(@PathVariable Long id) {
        return ResponseEntity.ok(postService.getPostByIdDTO(id));
    }

    // Update post only author
    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable Long id, @RequestBody Post post, Authentication auth) {
        String email = (String) auth.getPrincipal();
        Post updated = postService.updatePost(id, post.getTitle(), post.getContent(), email);
        return ResponseEntity.ok(updated);
    }

    // Delete post → author or admin
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable Long id, Authentication auth) {
        String email = (String) auth.getPrincipal();
        String role = auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        postService.deletePost(id, email, role);
        return ResponseEntity.ok().build();
    }
}

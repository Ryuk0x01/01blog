package com.blog.backend.controller;

import com.blog.backend.dto.CommentRequestDTO;
import com.blog.backend.dto.CommentResponseDTO;
import com.blog.backend.service.CommentService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping
    public ResponseEntity<CommentResponseDTO> addComment(@PathVariable Long postId,
            @Valid @RequestBody CommentRequestDTO comment,
            Authentication auth) {

        String email = (String) auth.getPrincipal();
        CommentResponseDTO created = commentService.addComment(postId, comment.getContent(), email);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    public ResponseEntity<Map<String,List<CommentResponseDTO>>> getComments(@PathVariable Long postId) {
        List<CommentResponseDTO> comments = commentService.getCommentsByPost(postId);
        // System.out.println("-----------------------------------Fetching comments for post ID: " + postId);
        // for (CommentResponseDTO comment : comments) {
        //     System.out.println("Comment ID: " + comment.getId()
        //             + ", Author: " + comment.getAuthorUsername()
        //             + ", Content: " + comment.getContent());
        // }
        return ResponseEntity.ok(Map.of("data", comments));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long postId,
            @PathVariable Long commentId,
            Authentication auth) {
        String email = (String) auth.getPrincipal();
        commentService.deleteComment(commentId, email);
        return ResponseEntity.ok(Map.of("status", "success"));
    }
}

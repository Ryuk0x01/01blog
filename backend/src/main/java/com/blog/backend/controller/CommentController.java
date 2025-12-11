package com.blog.backend.controller;

import com.blog.backend.entity.Comment;
import com.blog.backend.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    // Add comment → authenticated users
    @PostMapping
    public ResponseEntity<Comment> addComment(@PathVariable Long postId,
                                              @RequestBody Comment comment,
                                              Authentication auth) {
        String email = (String) auth.getPrincipal();
        Comment created = commentService.addComment(postId, comment.getContent(), email);
        return ResponseEntity.ok(created);
    }

    // Get all comments of a post → public
    @GetMapping
    public ResponseEntity<List<Comment>> getComments(@PathVariable Long postId) {
        List<Comment> comments = commentService.getCommentsByPost(postId);
        return ResponseEntity.ok(comments);
    }

    // Delete comment → author/Admin
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable Long postId,
                                           @PathVariable Long commentId,
                                           Authentication auth) {
        String email = (String) auth.getPrincipal();
        String role = auth.getAuthorities().iterator().next().getAuthority().replace("ROLE_", "");
        commentService.deleteComment(commentId, email, role);
        return ResponseEntity.ok().build();
    }
}

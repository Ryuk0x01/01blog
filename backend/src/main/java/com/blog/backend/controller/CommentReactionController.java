package com.blog.backend.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blog.backend.entity.Comment;
import com.blog.backend.entity.ReactionType;
import com.blog.backend.repository.CommentReactionRepository;
import com.blog.backend.repository.CommentRepository;
import com.blog.backend.service.CommentReactionService;

@RestController
@RequestMapping("/api/comments")
public class CommentReactionController {

    private final CommentReactionService reactionService;
    private final CommentReactionRepository reactionRepository;
    private final CommentRepository commentRepository;

    public CommentReactionController(CommentReactionService reactionService,
                                     CommentReactionRepository reactionRepository,
                                     CommentRepository commentRepository) {
        this.reactionService = reactionService;
        this.reactionRepository = reactionRepository;
        this.commentRepository = commentRepository;
    }

    @PostMapping("/{commentId}/like")
    public ResponseEntity<?> likeComment(@PathVariable Long commentId, Authentication auth) {
        String email = (String) auth.getPrincipal();
        reactionService.react(commentId, email, ReactionType.LIKE);
        return getReactions(commentId);
    }

    @PostMapping("/{commentId}/dislike")
    public ResponseEntity<?> dislikeComment(@PathVariable Long commentId, Authentication auth) {
        String email = (String) auth.getPrincipal();
        reactionService.react(commentId, email, ReactionType.DISLIKE);
        return getReactions(commentId);
    }

    @GetMapping("/{commentId}/reactions")
    public ResponseEntity<?> getReactions(@PathVariable Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        long likes = reactionRepository.countByCommentAndType(comment, ReactionType.LIKE);
        long dislikes = reactionRepository.countByCommentAndType(comment, ReactionType.DISLIKE);

        Map<String, Object> response = Map.of(
                "likes", likes,
                "dislikes", dislikes
        );
        return ResponseEntity.ok(response);
    }
}

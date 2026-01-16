package com.blog.backend.controller;

import com.blog.backend.entity.Post;
import com.blog.backend.entity.ReactionType;
import com.blog.backend.repository.PostReactionRepository;
import com.blog.backend.repository.PostRepository;
import com.blog.backend.service.PostReactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
public class PostReactionController {

    private final PostReactionService reactionService;
    private final PostReactionRepository reactionRepository;
    private final PostRepository postRepository;

    public PostReactionController(
            PostReactionService reactionService,
            PostReactionRepository reactionRepository,
            PostRepository postRepository) {
        this.reactionService = reactionService;
        this.reactionRepository = reactionRepository;
        this.postRepository = postRepository;
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<?> likePost(@PathVariable Long postId, Authentication auth) {
        String email = (String) auth.getPrincipal();
        reactionService.react(postId, email, ReactionType.LIKE);
        return getReactions(postId);
    }

    @PostMapping("/{postId}/dislike")
    public ResponseEntity<?> dislikePost(@PathVariable Long postId, Authentication auth) {
        String email = (String) auth.getPrincipal();
        reactionService.react(postId, email, ReactionType.DISLIKE);
        return getReactions(postId);
    }

    @GetMapping("/{postId}/reactions")
    public ResponseEntity<?> getReactions(@PathVariable Long postId) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        long likes = reactionRepository.countByPostAndType(post, ReactionType.LIKE);
        long dislikes = reactionRepository.countByPostAndType(post, ReactionType.DISLIKE);

        Map<String, Object> response = new HashMap<>();
        response.put("likes", likes);
        response.put("dislikes", dislikes);

        return ResponseEntity.ok(response);
    }
}

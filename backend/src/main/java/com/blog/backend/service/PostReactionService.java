package com.blog.backend.service;

import com.blog.backend.entity.*;
import com.blog.backend.repository.PostReactionRepository;
import com.blog.backend.repository.PostRepository;
import com.blog.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PostReactionService {

    private final PostReactionRepository reactionRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public PostReactionService(
            PostReactionRepository reactionRepository,
            PostRepository postRepository,
            UserRepository userRepository,
            NotificationService notificationService
    ) {
        this.reactionRepository = reactionRepository;
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    public void react(Long postId, String email, ReactionType type) {

        User user = userRepository.findByEmail(email);

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Optional<PostReaction> existing =
                reactionRepository.findByUserAndPost(user, post);

        if (existing.isPresent()) {
            PostReaction reaction = existing.get();

            if (reaction.getType() == type) {
                reactionRepository.delete(reaction);
            } else {
                reaction.setType(type);
                reactionRepository.save(reaction);
            }
        } else {
            PostReaction reaction = new PostReaction();
            reaction.setUser(user);
            reaction.setPost(post);
            reaction.setType(type);
            reactionRepository.save(reaction);
            
            if (type == ReactionType.LIKE) {
                notificationService.notifyLike(user, post);
            }
        }
    }
}

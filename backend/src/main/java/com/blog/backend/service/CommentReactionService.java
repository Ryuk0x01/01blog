package com.blog.backend.service;

import com.blog.backend.entity.*;
import com.blog.backend.repository.CommentReactionRepository;
import com.blog.backend.repository.CommentRepository;
import com.blog.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CommentReactionService {

    private final CommentReactionRepository reactionRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public CommentReactionService(CommentReactionRepository reactionRepository,
                                  CommentRepository commentRepository,
                                  UserRepository userRepository) {
        this.reactionRepository = reactionRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    public void react(Long commentId, String email, ReactionType type) {

        User user = userRepository.findByEmail(email);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        Optional<CommentReaction> existing =
                reactionRepository.findByUserAndComment(user, comment);

        if (existing.isPresent()) {
            CommentReaction reaction = existing.get();
            if (reaction.getType() == type) {
                reactionRepository.delete(reaction);
            } else {
                reaction.setType(type);
                reactionRepository.save(reaction);
            }
        } else {
            CommentReaction reaction = new CommentReaction();
            reaction.setUser(user);
            reaction.setComment(comment);
            reaction.setType(type);
            reactionRepository.save(reaction);
        }
    }
}

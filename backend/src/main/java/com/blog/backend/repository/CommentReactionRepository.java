package com.blog.backend.repository;

import com.blog.backend.entity.Comment;
import com.blog.backend.entity.CommentReaction;
import com.blog.backend.entity.ReactionType;
import com.blog.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentReactionRepository extends JpaRepository<CommentReaction, Long> {

    Optional<CommentReaction> findByUserAndComment(User user, Comment comment);

    long countByCommentAndType(Comment comment, ReactionType type);
}

package com.blog.backend.repository;

import com.blog.backend.entity.Post;
import com.blog.backend.entity.PostReaction;
import com.blog.backend.entity.ReactionType;
import com.blog.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostReactionRepository extends JpaRepository<PostReaction, Long> {

    Optional<PostReaction> findByUserAndPost(User user, Post post);

    long countByPostAndType(Post post, ReactionType type);
}

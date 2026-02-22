package com.blog.backend.repository;

import com.blog.backend.entity.Post;
import com.blog.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    List<Post> findAllByAuthor(User author);

    List<Post> findByAuthorInOrderByCreatedAtDesc(List<User> authors);

    long countByAuthor(User user);
}

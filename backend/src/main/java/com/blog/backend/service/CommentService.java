package com.blog.backend.service;

import com.blog.backend.entity.Comment;
import com.blog.backend.entity.Post;
import com.blog.backend.entity.User;
import com.blog.backend.repository.CommentRepository;
import com.blog.backend.repository.PostRepository;
import com.blog.backend.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public CommentService(CommentRepository commentRepository,
                          UserRepository userRepository,
                          PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    public Comment addComment(Long postId, String content, String userEmail) {
        User author = userRepository.findByEmail(userEmail);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setAuthor(author);
        comment.setPost(post);

        return commentRepository.save(comment);
    }

    public List<Comment> getCommentsByPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        return commentRepository.findAllByPost(post);
    }

    public void deleteComment(Long commentId, String userEmail, String role) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getAuthor().getEmail().equals(userEmail) && !role.equals("ADMIN")) {
            throw new AccessDeniedException("You are not allowed to delete this comment");
        }

        commentRepository.delete(comment);
    }
}

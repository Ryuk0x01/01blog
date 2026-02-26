package com.blog.backend.service;

import com.blog.backend.dto.CommentResponseDTO;
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
    private final NotificationService notificationService;

    public CommentService(CommentRepository commentRepository,
            UserRepository userRepository,
            PostRepository postRepository,
            NotificationService notificationService) {
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.notificationService = notificationService;
    }

    public CommentResponseDTO addComment(Long postId, String content, String userEmail) {
        User author = userRepository.findByEmail(userEmail);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        Comment comment = new Comment();
        comment.setContent(content);
        comment.setAuthor(author);
        comment.setPost(post);

        commentRepository.save(comment);
        notificationService.notifyComment(author, comment);

        return convertToDTO(comment);
    }

    public List<CommentResponseDTO> getCommentsByPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));

        List<Comment> comments = commentRepository.findAllByPost(post);

        List<CommentResponseDTO> dtoList = comments.stream()
                .map(this::convertToDTO)
                .toList();
        return dtoList;
    }

    public void deleteComment(Long commentId, String userEmail) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (!comment.getAuthor().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("You are not allowed to delete this comment");
        }

        commentRepository.delete(comment);
    }

    private CommentResponseDTO convertToDTO(Comment comment) {
        CommentResponseDTO dto = new CommentResponseDTO();
        dto.setId(comment.getId());
        dto.setContent(comment.getContent());
        dto.setAuthorUsername(comment.getAuthor().getUsername());
        dto.setPostId(comment.getPost().getId());
        dto.setCreatedAt(comment.getCreatedAt());

        return dto;
    }
}

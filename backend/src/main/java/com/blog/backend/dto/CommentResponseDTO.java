package com.blog.backend.dto;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentResponseDTO {
    private Long id;
    private String content;
    private String authorUsername;
    private Long postId;
    private LocalDateTime createdAt;
}

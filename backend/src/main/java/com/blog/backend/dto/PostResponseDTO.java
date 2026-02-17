package com.blog.backend.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class PostResponseDTO {

    private Long id;
    private String title;
    private String content;
    private String authorUsername;
    private String mediaUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private long likesCount;
    private long commentsCount;
    private boolean likedByCurrentUser;
}

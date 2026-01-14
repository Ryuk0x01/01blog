package com.blog.backend.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequestDTO {
    @NotBlank(message = "Content is required")
    private String content;
}

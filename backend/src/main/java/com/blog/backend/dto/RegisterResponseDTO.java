package com.blog.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterResponseDTO {
    private Long id;
    private String username;
    private String email;
    private String role;
}
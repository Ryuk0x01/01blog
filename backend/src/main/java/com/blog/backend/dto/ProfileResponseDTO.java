package com.blog.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ProfileResponseDTO {

    private Long id;
    private String username;
    // private String avatarUrl;

    private long postsCount;
    private long followersCount;
    private long followingCount;

    private boolean following;
}

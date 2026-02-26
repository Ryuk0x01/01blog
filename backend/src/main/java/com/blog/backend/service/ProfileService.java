package com.blog.backend.service;

import org.springframework.stereotype.Service;

import com.blog.backend.dto.ProfileResponseDTO;
import com.blog.backend.entity.User;
import com.blog.backend.repository.PostRepository;
import com.blog.backend.repository.UserFollowRepository;
import com.blog.backend.repository.UserRepository;

@Service
public class ProfileService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final UserFollowRepository userFollowRepository;

    public ProfileService(UserRepository userRepository, PostRepository postRepository,
            UserFollowRepository userFollowRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.userFollowRepository = userFollowRepository;
    }

    public ProfileResponseDTO getProfile(Long userId, String viewerEmail) {
        User profileUser = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));

        Long postsCount = postRepository.countByAuthor(profileUser);
        Long followerCount = userFollowRepository.countByFollowing(profileUser);
        Long followingCount = userFollowRepository.countByFollower(profileUser);

        boolean isFollowing = false;

        if (viewerEmail != null) {
            User viewer = userRepository.findByEmail(viewerEmail);
            isFollowing = userFollowRepository.existsByFollowerAndFollowing(viewer, profileUser);
        }


        return new ProfileResponseDTO(
            profileUser.getId(),
            profileUser.getUsername(),
            postsCount,
            followerCount,
            followingCount,
            isFollowing
        );

    }

}

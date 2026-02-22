package com.blog.backend.service;

import org.springframework.stereotype.Service;

import com.blog.backend.entity.User;
import com.blog.backend.entity.UserFollow;
import com.blog.backend.repository.UserFollowRepository;
import com.blog.backend.repository.UserRepository;
import com.blog.backend.service.NotificationService;


@Service
public class FollowService {

    private final UserRepository userRepository;
    private final UserFollowRepository followRepository;
    private final NotificationService notificationService;

    public FollowService(UserRepository userRepository,
                         UserFollowRepository followRepository,
                         NotificationService notificationService) {
        this.userRepository = userRepository;
        this.followRepository = followRepository;
        this.notificationService = notificationService;
    }

    public void follow(Long userIdToFollow, String email) {

        User follower = userRepository.findByEmail(email);
        User following = userRepository.findById(userIdToFollow)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (follower.getId().equals(following.getId())) {
            throw new RuntimeException("You can't follow yourself");
        }

        if (followRepository.existsByFollowerAndFollowing(follower, following)) {
            return;
        }

        UserFollow follow = new UserFollow();
        follow.setFollower(follower);
        follow.setFollowing(following);

        followRepository.save(follow);
        notificationService.notifyFollow(follower, following);
    }

    public void unfollow(Long userIdToUnfollow, String email) {

        User follower = userRepository.findByEmail(email);
        User following = userRepository.findById(userIdToUnfollow)
                .orElseThrow(() -> new RuntimeException("User not found"));

        followRepository.findByFollowerAndFollowing(follower, following)
                .ifPresent(followRepository::delete);
    }
}

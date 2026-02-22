package com.blog.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.blog.backend.entity.User;
import com.blog.backend.entity.UserFollow;

public interface UserFollowRepository extends JpaRepository<UserFollow, Long> {

    boolean existsByFollowerAndFollowing(User follower, User following);

    Optional<UserFollow> findByFollowerAndFollowing(User follower, User following);

    List<UserFollow> findByFollowing(User following);

    List<UserFollow> findByFollower(User follower);

    long countByFollowing(User user);
    long countByFollower(User user);
}


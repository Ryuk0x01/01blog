package com.blog.backend.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.blog.backend.entity.Notification;
import com.blog.backend.entity.NotificationType;
import com.blog.backend.entity.User;
import com.blog.backend.entity.UserFollow;
import com.blog.backend.repository.NotificationRepository;
import com.blog.backend.repository.UserFollowRepository;
import com.blog.backend.repository.UserRepository;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final UserFollowRepository userFollowRepository;

    public NotificationService(NotificationRepository notificationRepository,
                               UserRepository userRepository,
                               UserFollowRepository userFollowRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.userFollowRepository = userFollowRepository;
    }

    public List<Notification> getNotifications(String email) {
        User user = userRepository.findByEmail(email);
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(user);
    }

    public long getUnreadCount(String email) {
        User user = userRepository.findByEmail(email);
        return notificationRepository.countByRecipientAndReadFalse(user);
    }

    public void markAsRead(Long notificationId, String email) {
        User user = userRepository.findByEmail(email);
        Notification n = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        if (!n.getRecipient().getId().equals(user.getId())) {
            throw new RuntimeException("Not your notification");
        }
        n.setRead(true);
        notificationRepository.save(n);
    }

    public void markAllAsRead(String email) {
        User user = userRepository.findByEmail(email);
        List<Notification> notifications = notificationRepository.findByRecipientOrderByCreatedAtDesc(user);
        notifications.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(notifications);
    }

    public void notifyFollowers(User author, Long postId) {
        // Get all users who follow this author
        List<UserFollow> followers = userFollowRepository.findByFollowing(author);
        for (UserFollow follow : followers) {
            Notification n = new Notification();
            n.setRecipient(follow.getFollower());
            n.setActorUsername(author.getUsername());
            n.setType(NotificationType.NEW_POST);
            n.setMessage(author.getUsername() + " published a new post");
            n.setReferenceId(postId);
            notificationRepository.save(n);
        }
    }

    public void notifyFollow(User follower, User followed) {
        Notification n = new Notification();
        n.setRecipient(followed);
        n.setActorUsername(follower.getUsername());
        n.setType(NotificationType.FOLLOW);
        n.setMessage(follower.getUsername() + " started following you");
        n.setReferenceId(follower.getId());
        notificationRepository.save(n);
    }
    public void notifyLike(User actor, com.blog.backend.entity.Post post) {
        if (actor.getId().equals(post.getAuthor().getId())) return;
        Notification n = new Notification();
        n.setRecipient(post.getAuthor());
        n.setActorUsername(actor.getUsername());
        n.setType(NotificationType.LIKE);
        n.setMessage(actor.getUsername() + " liked your post: " + post.getTitle());
        n.setReferenceId(post.getId());
        notificationRepository.save(n);
    }

    public void notifyComment(User actor, com.blog.backend.entity.Comment comment) {
        if (actor.getId().equals(comment.getPost().getAuthor().getId())) return;
        Notification n = new Notification();
        n.setRecipient(comment.getPost().getAuthor());
        n.setActorUsername(actor.getUsername());
        n.setType(NotificationType.COMMENT);
        n.setMessage(actor.getUsername() + " commented on your post: " + comment.getPost().getTitle());
        n.setReferenceId(comment.getPost().getId());
        notificationRepository.save(n);
    }
}

package com.blog.backend.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blog.backend.entity.Notification;
import com.blog.backend.service.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<?> getNotifications(Authentication auth) {
        List<Notification> notifications = notificationService.getNotifications(auth.getName());
        // Map to safe response (avoid circular refs)
        var data = notifications.stream().map(n -> Map.of(
                "id", n.getId(),
                "actorUsername", n.getActorUsername(),
                "type", n.getType().name(),
                "message", n.getMessage(),
                "referenceId", n.getReferenceId() != null ? n.getReferenceId() : 0,
                "read", n.isRead(),
                "createdAt", n.getCreatedAt().toString()
        )).toList();
        return ResponseEntity.ok(Map.of("data", data));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<?> getUnreadCount(Authentication auth) {
        long count = notificationService.getUnreadCount(auth.getName());
        return ResponseEntity.ok(Map.of("count", count));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<?> markAsRead(@PathVariable Long id, Authentication auth) {
        notificationService.markAsRead(id, auth.getName());
        return ResponseEntity.ok(Map.of("status", "success"));
    }

    @PutMapping("/read-all")
    public ResponseEntity<?> markAllAsRead(Authentication auth) {
        notificationService.markAllAsRead(auth.getName());
        return ResponseEntity.ok(Map.of("status", "success"));
    }
}

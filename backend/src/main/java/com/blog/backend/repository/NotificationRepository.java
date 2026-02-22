package com.blog.backend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.blog.backend.entity.Notification;
import com.blog.backend.entity.User;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipientOrderByCreatedAtDesc(User recipient);

    long countByRecipientAndReadFalse(User recipient);
}

package com.hkouki._blog.repository;

import com.hkouki._blog.entity.Notification;
import com.hkouki._blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Find all notifications for a user ordered by newest first
    List<Notification> findByUserOrderByCreatedAtDesc(User user);

    // Find unread notifications for a user
    List<Notification> findByUserAndReadFalseOrderByCreatedAtDesc(User user);

    // Count unread notifications for a user
    long countByUserAndReadFalse(User user);
}

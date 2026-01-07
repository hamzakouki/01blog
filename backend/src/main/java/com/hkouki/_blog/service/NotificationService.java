package com.hkouki._blog.service;

import com.hkouki._blog.dto.NotificationResponse;
import com.hkouki._blog.entity.Notification;
import com.hkouki._blog.entity.Post;
import com.hkouki._blog.entity.User;
import com.hkouki._blog.exception.ResourceNotFoundException;
import com.hkouki._blog.repository.NotificationRepository;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserService userService;

    public NotificationService(
            NotificationRepository notificationRepository,
            UserService userService) {
        this.notificationRepository = notificationRepository;
        this.userService = userService;
    }

    /**
     * Create a notification for a user
     */
    @Transactional
    @SuppressWarnings("null")
    public void createNotification(User user, String message, String type, Post relatedPost, User relatedUser) {
        Notification notification = Notification.builder()
                .user(user)
                .message(message)
                .type(type)
                .read(false)
                .relatedPost(relatedPost)
                .relatedUser(relatedUser)
                .build();

        notificationRepository.save(notification);
    }

    /**
     * Get all notifications for the current user
     */
    public List<NotificationResponse> getMyNotifications() {
        User currentUser = userService.getCurrentUser();

        return notificationRepository.findByUserOrderByCreatedAtDesc(currentUser).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get unread notifications for the current user
     */
    public List<NotificationResponse> getUnreadNotifications() {
        User currentUser = userService.getCurrentUser();

        return notificationRepository.findByUserAndReadFalseOrderByCreatedAtDesc(currentUser).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get unread notification count for the current user
     */
    public long getUnreadCount() {
        User currentUser = userService.getCurrentUser();
        return notificationRepository.countByUserAndReadFalse(currentUser);
    }

    /**
     * Mark a notification as read
     */
    @Transactional
    public void markAsRead(@NonNull Long notificationId) {
        if (notificationId == null) {
            throw new IllegalArgumentException("Notification ID is required");
        }

        User currentUser = userService.getCurrentUser();

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        // Only the notification owner can mark it as read
        if (!notification.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("You do not have permission to modify this notification");
        }

        notification.setRead(true);
    }

    /**
     * Mark all notifications as read for the current user
     */
    @Transactional
    public void markAllAsRead() {
        User currentUser = userService.getCurrentUser();

        List<Notification> unreadNotifications = 
                notificationRepository.findByUserAndReadFalseOrderByCreatedAtDesc(currentUser);

        unreadNotifications.forEach(notification -> notification.setRead(true));
    }

    /**
     * Delete a notification
     */
    @Transactional
    @SuppressWarnings("null")
    public void deleteNotification(@NonNull Long notificationId) {
        if (notificationId == null) {
            throw new IllegalArgumentException("Notification ID is required");
        }

        User currentUser = userService.getCurrentUser();

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        // Only the notification owner can delete it
        if (!notification.getUser().getId().equals(currentUser.getId())) {
            throw new IllegalArgumentException("You do not have permission to delete this notification");
        }

        notificationRepository.delete(notification);
    }

    /**
     * Convert Notification entity to NotificationResponse DTO
     */
    private NotificationResponse convertToResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .message(notification.getMessage())
                .type(notification.getType())
                .read(notification.isRead())
                .relatedPostId(notification.getRelatedPost() != null ? notification.getRelatedPost().getId() : null)
                .relatedUserId(notification.getRelatedUser() != null ? notification.getRelatedUser().getId() : null)
                .relatedUsername(notification.getRelatedUser() != null ? notification.getRelatedUser().getUsername() : null)
                .createdAt(notification.getCreatedAt())
                .build();
    }
}

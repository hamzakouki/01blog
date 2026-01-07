package com.hkouki._blog.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // User who receives the notification
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User user;

    // Notification message
    @Column(nullable = false, length = 500)
    private String message;

    // Type of notification (NEW_POST, NEW_FOLLOWER, etc.)
    @Column(nullable = false, length = 50)
    private String type;

    // Has the user read this notification?
    @Column(nullable = false)
    private boolean read;

    // Related post (if applicable)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Post relatedPost;

    // Related user (if applicable, e.g., who followed you)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private User relatedUser;

    // When the notification was created
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

package com.hkouki._blog.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResponse {

    private Long id;
    private String message;
    private String type;
    private boolean read;
    
    // Related post info (if applicable)
    private Long relatedPostId;
    
    // Related user info (if applicable)
    private Long relatedUserId;
    private String relatedUsername;
    
    private LocalDateTime createdAt;
}

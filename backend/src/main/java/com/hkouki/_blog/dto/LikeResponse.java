package com.hkouki._blog.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class LikeResponse {

    private Long id;
    
    // Post info
    private Long postId;
    
    // User info
    private Long userId;
    private String username;
    
    private LocalDateTime createdAt;
}

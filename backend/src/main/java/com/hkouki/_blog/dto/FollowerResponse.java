package com.hkouki._blog.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FollowerResponse {

    private Long id;
    
    // Follower info
    private Long followerId;
    private String followerUsername;
    
    // Following info
    private Long followingId;
    private String followingUsername;
    
    private LocalDateTime createdAt;
}

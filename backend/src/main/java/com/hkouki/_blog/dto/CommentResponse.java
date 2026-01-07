package com.hkouki._blog.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class CommentResponse {

    private Long id;
    private String content;
    
    // Post info
    private Long postId;
    
    // Author info
    private Long authorId;
    private String authorUsername;
    
    private LocalDateTime createdAt;
}

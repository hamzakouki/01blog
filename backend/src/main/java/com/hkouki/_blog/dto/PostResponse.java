package com.hkouki._blog.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class PostResponse {
    private Long id;
    private String content;
    private String mediaUrl;
    private Long authorId;
    private String authorUsername;
    private String authorRole;
    private LocalDateTime createdAt;
}

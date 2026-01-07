package com.hkouki._blog.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ReportResponse {

    private Long id;

    // reported user info
    private Long reportedUserId;
    private String reportedUsername;

    // reporter info
    private Long reporterId;
    private String reporterUsername;

    private String reason;
    private boolean handled;
    private LocalDateTime createdAt;
}

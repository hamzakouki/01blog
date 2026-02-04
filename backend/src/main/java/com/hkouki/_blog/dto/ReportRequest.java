package com.hkouki._blog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReportRequest {

    private Long reportedUserId; // nullable
    private Long postId;         // nullable

    @NotBlank(message = "Reason is required")
    private String reason;
}

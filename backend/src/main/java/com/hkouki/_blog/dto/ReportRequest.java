package com.hkouki._blog.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReportRequest {
    @NotNull(message = "Reported user ID is required")
    private Long reportedUserId;
    @NotBlank(message = "Reason is required")
    private String reason;
}

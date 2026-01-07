package com.hkouki._blog.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserSummaryResponse {
    
    private Long id;
    private String username;
    private String email;
}

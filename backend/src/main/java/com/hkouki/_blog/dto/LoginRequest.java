package com.hkouki._blog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
// import jakarta.validation.constraints.Email;

@Data
public class LoginRequest {
    
    @NotBlank(message = "Identifier is required")
    private String identifier; 
    
    @NotBlank(message = "Password is required")
    private String password;
}

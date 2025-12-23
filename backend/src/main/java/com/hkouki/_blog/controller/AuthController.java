package com.hkouki._blog.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hkouki._blog.dto.ApiResponse;
import com.hkouki._blog.dto.LoginRequest;
import com.hkouki._blog.dto.LoginResponse;
import com.hkouki._blog.dto.RegisterRequest;
import com.hkouki._blog.service.AuthService;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // register endpoint
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(@Valid @RequestBody RegisterRequest dto) {
        authService.register(dto);

        ApiResponse<Void> response = new ApiResponse<>("success", null, "User registered successfully");

        return ResponseEntity.ok(response);
    }

    // login endpoint
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest dto) {
        LoginResponse loginResponse = authService.login(dto);

        ApiResponse<LoginResponse> response = new ApiResponse<>("success", loginResponse, "Login successful");

        return ResponseEntity.ok(response);
    }

}

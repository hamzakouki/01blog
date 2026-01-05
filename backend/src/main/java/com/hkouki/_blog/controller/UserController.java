package com.hkouki._blog.controller;

import com.hkouki._blog.dto.ApiResponse;
import com.hkouki._blog.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    @PutMapping("/ban/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> banUser(@PathVariable Long userId) {
        // System.out.println("Ban user with ID: " + userId);
        String message = userService.banUser(userId);
        return ResponseEntity.ok(new ApiResponse<>("success", userId.toString(), message));
    }

    @PutMapping("/unban/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> unbanUser(@PathVariable Long userId) {
        // System.out.println("Ban user with ID: " + userId);
        String message = userService.unbanUser(userId);
        return ResponseEntity.ok(new ApiResponse<>("success", userId.toString(), message));
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.ok(new ApiResponse<>("success", userId.toString(), "User deleted successfully"));
    }
}

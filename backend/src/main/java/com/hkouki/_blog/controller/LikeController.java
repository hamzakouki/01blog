package com.hkouki._blog.controller;

import com.hkouki._blog.dto.ApiResponse;
import com.hkouki._blog.dto.LikeResponse;
import com.hkouki._blog.service.LikeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/likes")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    /**
     * Toggle like on a post
     */
    @PostMapping("/{postId}")
    public ResponseEntity<ApiResponse<LikeResponse>> toggleLike(@PathVariable Long postId) {
        LikeResponse like = likeService.toggleLike(postId);
        String message = like != null ? "Post liked successfully" : "Post unliked successfully";
        return ResponseEntity.ok(new ApiResponse<>("success", like, message));
    }

    /**
     * Check if current user liked a post
     */
    @GetMapping("/{postId}/status")
    public ResponseEntity<ApiResponse<Map<String, Boolean>>> isLiked(@PathVariable Long postId) {
        boolean isLiked = likeService.isLiked(postId);
        return ResponseEntity.ok(new ApiResponse<>("success", Map.of("isLiked", isLiked), "Like status retrieved successfully"));
    }

    /**
     * Get like count for a post
     */
    @GetMapping("/{postId}/count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getLikeCount(@PathVariable Long postId) {
        long count = likeService.getLikeCount(postId);
        return ResponseEntity.ok(new ApiResponse<>("success", Map.of("likeCount", count), "Like count retrieved successfully"));
    }
}

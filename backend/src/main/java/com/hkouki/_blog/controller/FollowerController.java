package com.hkouki._blog.controller;

import com.hkouki._blog.dto.ApiResponse;
import com.hkouki._blog.dto.FollowerResponse;
import com.hkouki._blog.dto.UserSummaryResponse;
import com.hkouki._blog.service.FollowerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/followers")
public class FollowerController {

    private final FollowerService followerService;

    public FollowerController(FollowerService followerService) {
        this.followerService = followerService;
    }

    // Follow a user
    @PostMapping("/{userId}")
    public ResponseEntity<ApiResponse<FollowerResponse>> followUser(@PathVariable Long userId) {
        FollowerResponse response = followerService.followUser(userId);
        return ResponseEntity.ok(new ApiResponse<>("success", response, "Successfully followed user"));
    }

    // Unfollow a user
    @DeleteMapping("/{userId}")
    public ResponseEntity<ApiResponse<String>> unfollowUser(@PathVariable Long userId) {
        followerService.unfollowUser(userId);
        return ResponseEntity.ok(new ApiResponse<>("success", null, "Successfully unfollowed user"));
    }

    // Get followers of a user
    @GetMapping("/{userId}/followers")
    public ResponseEntity<ApiResponse<UserSummaryResponse[]>> getFollowers(@PathVariable Long userId) {
        UserSummaryResponse[] followers = followerService.getFollowers(userId);
        return ResponseEntity.ok(new ApiResponse<>("success", followers, "Fetched followers successfully"));
    }

    // Get users that a user is following
    @GetMapping("/{userId}/following")
    public ResponseEntity<ApiResponse<UserSummaryResponse[]>> getFollowing(@PathVariable Long userId) {
        UserSummaryResponse[] following = followerService.getFollowing(userId);
        return ResponseEntity.ok(new ApiResponse<>("success", following, "Fetched following successfully"));
    }

    // Check if user is following another user
    @GetMapping("/{followerId}/is-following/{followingId}")
    public ResponseEntity<ApiResponse<Boolean>> isFollowing(
            @PathVariable Long followerId,
            @PathVariable Long followingId) {
        boolean isFollowing = followerService.isFollowing(followerId, followingId);
        return ResponseEntity.ok(new ApiResponse<>("success", isFollowing, "Checked follow status"));
    }

    // Get follower count
    @GetMapping("/{userId}/followers/count")
    public ResponseEntity<ApiResponse<Long>> getFollowerCount(@PathVariable Long userId) {
        long count = followerService.getFollowerCount(userId);
        return ResponseEntity.ok(new ApiResponse<>("success", count, "Fetched follower count"));
    }

    // Get following count
    @GetMapping("/{userId}/following/count")
    public ResponseEntity<ApiResponse<Long>> getFollowingCount(@PathVariable Long userId) {
        long count = followerService.getFollowingCount(userId);
        return ResponseEntity.ok(new ApiResponse<>("success", count, "Fetched following count"));
    }
}

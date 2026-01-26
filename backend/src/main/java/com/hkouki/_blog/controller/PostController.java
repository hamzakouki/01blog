package com.hkouki._blog.controller;

import com.hkouki._blog.dto.ApiResponse;
import com.hkouki._blog.dto.PostRequest;
import com.hkouki._blog.dto.PostResponse;
import com.hkouki._blog.service.PostService;
import com.hkouki._blog.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService, UserService userService) {
        this.postService = postService;
    }

    // Create a new post
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<PostResponse>> createPost(@Valid @ModelAttribute PostRequest request) throws Exception {
        // System.out.println("==================");
        PostResponse post = postService.createPost(request);
        return ResponseEntity.ok(new ApiResponse<>("success", post, "Post created successfully"));
    }

    // get post by id
    @GetMapping("/{postId}/details")
    public ResponseEntity<ApiResponse<PostResponse>> getPostById(@PathVariable Long postId) {
        PostResponse post = postService.getPostById(postId);
        return ResponseEntity.ok(new ApiResponse<>("success", post, "Post retrieved successfully"));
    }


    // Get all posts (feed)
    @GetMapping("/feed")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getFeed() {
        // System.out.println("====================alooooooo======================");
        List<PostResponse> posts = postService.getFeed();
        return ResponseEntity.ok(new ApiResponse<>("success", posts, "Feed retrieved successfully"));
    }

    // Get posts for a specific user block page
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getUserPosts(@PathVariable Long userId) {
        List<PostResponse> posts = postService.getPostsByUser(userId);
        return ResponseEntity.ok(new ApiResponse<>("success", posts, "User posts retrieved successfully"));
    }

    // this endpoint is only accessible by ADMIN to hidde inappropriate posts
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/hidde/{postId}")
    public ResponseEntity<ApiResponse<String>> hiddePost(@PathVariable Long postId) {
        // System.out.println("Hiding post with ID: " + postId);
        String message = postService.hiddePost(postId);
        return ResponseEntity.ok(new ApiResponse<>("success", postId.toString(), message));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/unhidde/{postId}")
    public ResponseEntity<ApiResponse<String>> unhiddePost(@PathVariable Long postId) {
        String message = postService.unhiddePost(postId);
        return ResponseEntity.ok(new ApiResponse<>("success", postId.toString(), message));
    }

    // @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<String>> deletePost(@PathVariable Long postId) {
        postService.deletePost(postId);
        return ResponseEntity.ok(new ApiResponse<>("success", postId.toString(), "Post "+postId+" deleted successfully"));
    }
}

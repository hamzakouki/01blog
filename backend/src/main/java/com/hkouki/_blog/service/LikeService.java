package com.hkouki._blog.service;

import com.hkouki._blog.dto.LikeResponse;
import com.hkouki._blog.entity.Like;
import com.hkouki._blog.entity.Post;
import com.hkouki._blog.entity.User;
import com.hkouki._blog.exception.ResourceNotFoundException;
import com.hkouki._blog.repository.LikeRepository;
import com.hkouki._blog.repository.PostRepository;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class LikeService {

    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final UserService userService;

    public LikeService(
            LikeRepository likeRepository,
            PostRepository postRepository,
            UserService userService) {
        this.likeRepository = likeRepository;
        this.postRepository = postRepository;
        this.userService = userService;
    }

    /**
     * Toggle like on a post (like if not liked, unlike if already liked)
     */
    @Transactional
    @SuppressWarnings("null")
    public LikeResponse toggleLike(@NonNull Long postId) {
        if (postId == null) {
            throw new IllegalArgumentException("Post ID is required");
        }

        User user = userService.getCurrentUser();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        Optional<Like> existingLike = likeRepository.findByPostAndUser(post, user);

        if (existingLike.isPresent()) {
            // Unlike - delete the like
            likeRepository.delete(existingLike.get());
            return null; // Return null to indicate unlike
        } else {
            // Like - create new like
            Like newLike = Like.builder()
                    .post(post)
                    .user(user)
                    .build();

            Like savedLike = likeRepository.save(newLike);

            return convertToResponse(savedLike);
        }
    }

    /**
     * Check if current user has liked a post
     */
    public boolean isLiked(@NonNull Long postId) {
        if (postId == null) {
            throw new IllegalArgumentException("Post ID is required");
        }

        User user = userService.getCurrentUser();

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        return likeRepository.existsByPostAndUser(post, user);
    }

    /**
     * Get like count for a post
     */
    public long getLikeCount(@NonNull Long postId) {
        if (postId == null) {
            throw new IllegalArgumentException("Post ID is required");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        return likeRepository.countByPost(post);
    }

    /**
     * Convert Like entity to LikeResponse DTO
     */
    private LikeResponse convertToResponse(Like like) {
        return LikeResponse.builder()
                .id(like.getId())
                .postId(like.getPost().getId())
                .userId(like.getUser().getId())
                .username(like.getUser().getUsername())
                .createdAt(like.getCreatedAt())
                .build();
    }
}

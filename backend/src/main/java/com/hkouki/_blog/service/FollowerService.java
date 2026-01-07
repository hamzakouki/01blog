package com.hkouki._blog.service;

import com.hkouki._blog.dto.FollowerResponse;
import com.hkouki._blog.dto.UserSummaryResponse;
import com.hkouki._blog.entity.Follower;
import com.hkouki._blog.entity.User;
import com.hkouki._blog.exception.ResourceNotFoundException;
import com.hkouki._blog.repository.FollowerRepository;
import com.hkouki._blog.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service layer for managing follower/following relationships between users.
 * Handles subscription logic, follower lists, and follow statistics.
 */
@Service
public class FollowerService {

    private final FollowerRepository followerRepository;
    private final UserRepository userRepository;
    private final UserService userService;

    public FollowerService(
            FollowerRepository followerRepository,
            UserRepository userRepository,
            UserService userService) {
        this.followerRepository = followerRepository;
        this.userRepository = userRepository;
        this.userService = userService;
    }

    /**
     * Creates a follow relationship between the current user and another user.
     * 
     * @param followingId The ID of the user to follow
     * @return FollowerResponse containing the created follow relationship details
     * @throws IllegalArgumentException if followingId is null, user tries to follow themselves, or already following
     * @throws ResourceNotFoundException if the user to follow does not exist
     */
    @Transactional
    public FollowerResponse followUser(Long followingId) {
        if (followingId == null) {
            throw new IllegalArgumentException("User ID is required");
        }

        User follower = userService.getCurrentUser();

        if (follower.getId().equals(followingId)) {
            throw new IllegalArgumentException("You cannot follow yourself");
        }

        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if already following
        if (followerRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new IllegalArgumentException("You are already following this user");
        }

        Follower newFollower = Follower.builder()
                .follower(follower)
                .following(following)
                .build();

        @SuppressWarnings("null")
        Follower savedFollower = followerRepository.save(newFollower);

        return convertToResponse(savedFollower);
    }

    /**
     * Removes a follow relationship between the current user and another user.
     * 
     * @param followingId The ID of the user to unfollow
     * @throws IllegalArgumentException if followingId is null
     * @throws ResourceNotFoundException if the user or follow relationship does not exist
     */
    @Transactional
    @SuppressWarnings("null")
    public void unfollowUser(Long followingId) {
        if (followingId == null) {
            throw new IllegalArgumentException("User ID is required");
        }

        User follower = userService.getCurrentUser();

        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Follower followerEntity = followerRepository.findByFollowerAndFollowing(follower, following)
                .orElseThrow(() -> new ResourceNotFoundException("Follow relationship not found"));

        followerRepository.delete(followerEntity);
    }

    /**
     * Retrieves a list of all users who are following the specified user.
     * 
     * @param userId The ID of the user whose followers to retrieve
     * @return Array of UserSummaryResponse containing follower information
     * @throws IllegalArgumentException if userId is null
     * @throws ResourceNotFoundException if the user does not exist
     */
    public UserSummaryResponse[] getFollowers(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return followerRepository.findByFollowing(user).stream()
                .map(f -> UserSummaryResponse.builder()
                        .id(f.getFollower().getId())
                        .username(f.getFollower().getUsername())
                        .email(f.getFollower().getEmail())
                        .build())
                .toArray(UserSummaryResponse[]::new);
    }

    /**
     * Retrieves a list of all users that the specified user is following.
     * 
     * @param userId The ID of the user whose following list to retrieve
     * @return Array of UserSummaryResponse containing users being followed
     * @throws IllegalArgumentException if userId is null
     * @throws ResourceNotFoundException if the user does not exist
     */
    public UserSummaryResponse[] getFollowing(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return followerRepository.findByFollower(user).stream()
                .map(f -> UserSummaryResponse.builder()
                        .id(f.getFollowing().getId())
                        .username(f.getFollowing().getUsername())
                        .email(f.getFollowing().getEmail())
                        .build())
                .toArray(UserSummaryResponse[]::new);
    }

    /**
     * Checks if one user is following another user.
     * 
     * @param followerId The ID of the potential follower
     * @param followingId The ID of the potential user being followed
     * @return true if follower is following the other user, false otherwise
     * @throws IllegalArgumentException if either followerId or followingId is null
     * @throws ResourceNotFoundException if either user does not exist
     */
    public boolean isFollowing(Long followerId, Long followingId) {
        if (followerId == null || followingId == null) {
            throw new IllegalArgumentException("User IDs are required");
        }

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new ResourceNotFoundException("Follower not found"));
        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new ResourceNotFoundException("Following user not found"));

        return followerRepository.existsByFollowerAndFollowing(follower, following);
    }

    /**
     * Counts the total number of followers for a specific user.
     * 
     * @param userId The ID of the user whose followers to count
     * @return The number of followers
     * @throws IllegalArgumentException if userId is null
     * @throws ResourceNotFoundException if the user does not exist
     */
    public long getFollowerCount(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return followerRepository.countByFollowing(user);
    }

    /**
     * Counts the total number of users that a specific user is following.
     * 
     * @param userId The ID of the user whose following count to retrieve
     * @return The number of users being followed
     * @throws IllegalArgumentException if userId is null
     * @throws ResourceNotFoundException if the user does not exist
     */
    public long getFollowingCount(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return followerRepository.countByFollower(user);
    }

    /**
     * Converts a Follower entity to a FollowerResponse DTO.
     * 
     * @param follower The Follower entity to convert
     * @return FollowerResponse containing the follower relationship data
     */
    private FollowerResponse convertToResponse(Follower follower) {
        return FollowerResponse.builder()
                .id(follower.getId())
                .followerId(follower.getFollower().getId())
                .followerUsername(follower.getFollower().getUsername())
                .followingId(follower.getFollowing().getId())
                .followingUsername(follower.getFollowing().getUsername())
                .createdAt(follower.getCreatedAt())
                .build();
    }
}

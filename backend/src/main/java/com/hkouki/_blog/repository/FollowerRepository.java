package com.hkouki._blog.repository;

import com.hkouki._blog.entity.Follower;
import com.hkouki._blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FollowerRepository extends JpaRepository<Follower, Long> {

    // Find all followers of a user (people who follow this user)
    List<Follower> findByFollowing(User following);

    // Find all users that a user is following
    List<Follower> findByFollower(User follower);

    // Check if follower is following another user
    Optional<Follower> findByFollowerAndFollowing(User follower, User following);

    // Check if follow relationship exists
    boolean existsByFollowerAndFollowing(User follower, User following);

    // Count followers of a user
    long countByFollowing(User following);

    // Count how many users a user is following
    long countByFollower(User follower);
}

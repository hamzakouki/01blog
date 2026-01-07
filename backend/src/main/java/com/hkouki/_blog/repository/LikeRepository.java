package com.hkouki._blog.repository;

import com.hkouki._blog.entity.Like;
import com.hkouki._blog.entity.Post;
import com.hkouki._blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    // Check if a user has liked a post
    boolean existsByPostAndUser(Post post, User user);

    // Find a specific like by post and user
    Optional<Like> findByPostAndUser(Post post, User user);

    // Count likes for a specific post
    long countByPost(Post post);
}

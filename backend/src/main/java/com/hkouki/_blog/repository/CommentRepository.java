package com.hkouki._blog.repository;

import com.hkouki._blog.entity.Comment;
import com.hkouki._blog.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // Find all comments for a specific post
    List<Comment> findByPostOrderByCreatedAtDesc(Post post);

    // Count comments for a specific post
    long countByPost(Post post);
}

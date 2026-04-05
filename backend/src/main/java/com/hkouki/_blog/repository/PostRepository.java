package com.hkouki._blog.repository;

import com.hkouki._blog.entity.Post;
import com.hkouki._blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // Fetch all posts ordered by creation date descending (newest first)
    List<Post> findAllByOrderByCreatedAtDesc();

    // Fetch all posts by a specific author ordered by creation date descending
    List<Post> findByAuthorIdOrderByCreatedAtDesc(Long authorId);

    // Fetch posts from a list of authors, only those that are not hidden
    List<Post> findByAuthorInAndHiddenFalseOrderByCreatedAtDesc(List<User> authors);

    // ✅ Fetch only posts that are not hidden
    List<Post> findByHiddenFalseOrderByCreatedAtDesc();
}

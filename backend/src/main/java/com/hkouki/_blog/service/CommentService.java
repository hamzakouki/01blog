package com.hkouki._blog.service;

import com.hkouki._blog.dto.CommentRequest;
import com.hkouki._blog.dto.CommentResponse;
import com.hkouki._blog.entity.Comment;
import com.hkouki._blog.entity.Post;
import com.hkouki._blog.entity.User;
import com.hkouki._blog.exception.ResourceNotFoundException;
import com.hkouki._blog.repository.CommentRepository;
import com.hkouki._blog.repository.PostRepository;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserService userService;

    public CommentService(CommentRepository commentRepository,PostRepository postRepository, UserService userService) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
        this.userService = userService;
    }

    /**
     * Create a new comment on a post
     */
    @Transactional
    public CommentResponse createComment(CommentRequest request) {
        User author = userService.getCurrentUser();

        Long postId = request.getPostId();
        if (postId == null) {
            throw new IllegalArgumentException("Post ID is required");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        Comment comment = Comment.builder()
                .content(request.getContent())
                .post(post)
                .author(author)
                .build();

        @SuppressWarnings("null")
        Comment savedComment = commentRepository.save(comment);

        return convertToResponse(savedComment);
    }

    /**
     * Get all comments for a specific post
     */
    public List<CommentResponse> getCommentsByPost(@NonNull Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        return commentRepository.findByPostOrderByCreatedAtDesc(post).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get a single comment by ID
     */
    public CommentResponse getCommentById(@NonNull Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        return convertToResponse(comment);
    }

    /**
     * Delete a comment by its ID
     */
    @Transactional
    @SuppressWarnings("null")
    public void deleteComment(@NonNull Long commentId) {
        User currentUser = userService.getCurrentUser();

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        // Only comment author or admin can delete
        if (!comment.getAuthor().getId().equals(currentUser.getId()) 
                && !currentUser.getRole().name().equals("ADMIN")) {
            throw new IllegalArgumentException("You do not have permission to delete this comment");
        }

        commentRepository.delete(comment);
    }

    /**
     * Convert Comment entity to CommentResponse DTO
     */
    private CommentResponse convertToResponse(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .postId(comment.getPost().getId())
                .authorId(comment.getAuthor().getId())
                .authorUsername(comment.getAuthor().getUsername())
                .createdAt(comment.getCreatedAt())
                .build();
    }
}

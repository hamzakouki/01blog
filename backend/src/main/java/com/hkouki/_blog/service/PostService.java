package com.hkouki._blog.service;

import com.hkouki._blog.dto.PostRequest;
import com.hkouki._blog.dto.PostResponse;
import com.hkouki._blog.entity.Post;
import com.hkouki._blog.entity.User;
import com.hkouki._blog.exception.ResourceNotFoundException;
import com.hkouki._blog.repository.PostRepository;
import com.hkouki._blog.repository.CommentRepository;
import com.hkouki._blog.repository.LikeRepository;
import lombok.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.stream.Collectors;
import java.io.IOException;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final MediaService mediaService;
    private final UserService userService;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;

    public PostService(
            PostRepository postRepository, 
            MediaService mediaService, 
            UserService userService,
            CommentRepository commentRepository,
            LikeRepository likeRepository) {
        this.postRepository = postRepository;
        this.mediaService = mediaService;
        this.userService = userService;
        this.commentRepository = commentRepository;
        this.likeRepository = likeRepository;
    }

    // Create a post with optional media upload
    public PostResponse createPost(PostRequest request) throws IOException {
        User author = userService.getCurrentUser();
        Post post = Post.builder().content(request.getContent()).author(author).hidden(false).build();
        MultipartFile media = request.getMedia();
        if (media != null && !media.isEmpty()) {
            // Upload media to Cloudinary
            String mediaUrl = mediaService.uploadFile(media);
            post.setMediaUrl(mediaUrl);
        }

        @SuppressWarnings("null")
        Post savedpost = postRepository.save(post);
        return mapToPostResponse(savedpost);
    }

    // get post by id
    public PostResponse getPostById(@NonNull Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        return mapToPostResponse(post);
    }

    // delete a post by its ID
    @SuppressWarnings("null")
    public void deletePost(@NonNull Long postId) {
        User currentUser = userService.getCurrentUser();
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        if (!post.getAuthor().getId().equals(currentUser.getId()) && !currentUser.getRole().name().equals("ADMIN")) {
            throw new ResourceNotFoundException("You do not have permission to delete this post.");
        }
        postRepository.delete(post);
    }

    // Hide a post by its ID
    public String hiddePost(@NonNull Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        if (post.isHidden() == true) {
            return "Post with ID " + postId + " is already hidden.";
        }

        post.setHidden(true);
        postRepository.save(post);
        return "Post with ID " + postId + " has been hidden.";
    }

    // Unhide a post by its ID
    public String unhiddePost(@NonNull Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postId));
        if (post.isHidden() == false) {
            return "Post with ID " + postId + " is not hidden.";
        }
        post.setHidden(false);
        postRepository.save(post);
        return "Post with ID " + postId + " has been unhidden.";
    }

    // Fetch all posts for feed (all posts)
    public List<PostResponse> getFeed() {
        return postRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(post -> mapToPostResponse(post))
                .collect(Collectors.toList());
    }

    // Fetch posts by a specific user
    public List<PostResponse> getPostsByUser(Long userId) {
        return postRepository.findByAuthorIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(post -> mapToPostResponse(post))
                .collect(Collectors.toList());
    }

    // change post type into post response
    public PostResponse mapToPostResponse(Post post) {
        long commentCount = commentRepository.countByPost(post);
        long likeCount = likeRepository.countByPost(post);
        
        return PostResponse.builder()
                .id(post.getId())
                .content(post.getContent())
                .mediaUrl(post.getMediaUrl())
                .authorId(post.getAuthor().getId())
                .authorUsername(post.getAuthor().getUsername())
                .authorRole(post.getAuthor().getRole().name())
                .createdAt(post.getCreatedAt())
                .commentCount(commentCount)
                .likeCount(likeCount)
                .build();
    }
}

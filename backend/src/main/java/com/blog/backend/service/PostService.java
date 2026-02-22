package com.blog.backend.service;

import com.blog.backend.dto.PostRequestDTO;
import com.blog.backend.dto.PostResponseDTO;
import com.blog.backend.entity.Post;
import com.blog.backend.entity.ReactionType;
import com.blog.backend.entity.User;
import com.blog.backend.entity.UserFollow;
import com.blog.backend.repository.CommentRepository;
import com.blog.backend.repository.PostReactionRepository;
import com.blog.backend.repository.PostRepository;
import com.blog.backend.repository.UserFollowRepository;
import com.blog.backend.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final long MAX_SIZE = 10 * 1024 * 1024; // 10MB

    private final PostReactionRepository postReactionRepository;
    private final CommentRepository commentRepository;
    private final NotificationService notificationService;
    private final UserFollowRepository userFollowRepository;

    private final String uploadDir = "uploads/";

    public PostService(PostRepository postRepository, UserRepository userRepository, PostReactionRepository postReactionRepository, CommentRepository commentRepository, NotificationService notificationService, UserFollowRepository userFollowRepository) {
        this.postRepository = postRepository;
        this.postReactionRepository = postReactionRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        this.userFollowRepository = userFollowRepository;
    }

    // Create post → any user
    public PostResponseDTO createPost(PostRequestDTO dto, String userEmail) throws Exception {
        User author = userRepository.findByEmail(userEmail);
        if (author == null)
            throw new RuntimeException("User not found");
        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        post.setAuthor(author);

        MultipartFile file = dto.getFile();
        if (file != null && !file.isEmpty()) {

            if (file.getSize() > MAX_SIZE) {
                throw new RuntimeException("File too large. Max 10MB allowed.");
            }

            String original = file.getOriginalFilename();
            if (original == null || !original.toLowerCase().matches(".*\\.(jpg|jpeg|png|gif|mp4|avi|mov)$")) {
                throw new RuntimeException("Invalid file extension");
            }

            String fileName = System.currentTimeMillis() + "_" + original;
            Path filePath = Paths.get(uploadDir + fileName);
            Files.createDirectories(filePath.getParent());
            file.transferTo(filePath);
            post.setMediaUrl("/" + uploadDir + fileName); // URL path
        }

        postRepository.save(post);
        notificationService.notifyFollowers(author, post.getId());
        return convertToDTO(post, userEmail);
    }

    // Get all posts → public
    public List<PostResponseDTO> getAllPosts(String userEmail) {
        List<Post> posts = postRepository.findAll();
        List<PostResponseDTO> dtoList = posts.stream()
                .filter(post -> !post.isHidden())
                .map(post -> convertToDTO(post, userEmail))
                .toList();
        return dtoList;
    }

    // Get posts by user
    public List<PostResponseDTO> getPostsByUser(Long userId, String viewerEmail) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Post> posts = postRepository.findAllByAuthor(author);
        return posts.stream()
                .filter(post -> !post.isHidden() || author.getEmail().equals(viewerEmail))
                .map(post -> convertToDTO(post, viewerEmail))
                .toList();
    }

    // Get feed posts (from followed users + self)
    public List<PostResponseDTO> getFeedPosts(String userEmail) {
        User currentUser = userRepository.findByEmail(userEmail);
        List<UserFollow> following = userFollowRepository.findByFollower(currentUser);
        List<User> authors = new java.util.ArrayList<>();
        authors.add(currentUser);
        following.forEach(f -> authors.add(f.getFollowing()));
        List<Post> posts = postRepository.findByAuthorInOrderByCreatedAtDesc(authors);
        return posts.stream()
                .filter(post -> !post.isHidden() || post.getAuthor().getEmail().equals(userEmail))
                .map(post -> convertToDTO(post, userEmail))
                .toList();
    }

    // Get single post → public
    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

    public PostResponseDTO getPostByIdDTO(Long id, String userEmail) {
        Post post = getPostById(id);
        return convertToDTO(post, userEmail);
    }

    // Update post → only author
    public PostResponseDTO updatePost(Long id, PostRequestDTO dto, String userEmail) throws Exception {
        Post post = getPostById(id);
        // check if author
        if (!post.getAuthor().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("Only the author can update this post");
        }

        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());

        // handle file
        MultipartFile file = dto.getFile();
        if (file != null && !file.isEmpty()) {

            if (file.getSize() > MAX_SIZE) {
                throw new RuntimeException("File too large. Max 10MB allowed.");
            }

            String original = file.getOriginalFilename();
            if (original == null || !original.toLowerCase().matches(".*\\.(jpg|jpeg|png|gif|mp4|avi|mov)$")) {
                throw new RuntimeException("Invalid file extension");
            }

            String fileName = System.currentTimeMillis() + "_" + original;
            Path filePath = Paths.get(uploadDir + fileName);
            Files.createDirectories(filePath.getParent());
            file.transferTo(filePath);
            post.setMediaUrl("/" + uploadDir + fileName);
        }

        postRepository.save(post);
        return convertToDTO(post, userEmail);
    }

    // Delete post → author or admin
    public void deletePost(Long id, String userEmail, String role) {
        Post post = getPostById(id);

        if (post.getAuthor().getEmail().equals(userEmail)) {
            postRepository.delete(post);
        } else if ("ADMIN".equals(role)) {
            postRepository.delete(post);
        } else {
            throw new AccessDeniedException("You are not allowed to delete this post");
        }
    }

    public PostResponseDTO convertToDTO(Post post, String currentUsername) {
        PostResponseDTO dto = new PostResponseDTO();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setAuthorId(post.getAuthor().getId());
        dto.setAuthorUsername(post.getAuthor().getUsername());
        dto.setMediaUrl(post.getMediaUrl());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());

        // COUNT LIKES
        long likesCount = postReactionRepository
                .countByPostIdAndType(post.getId(), ReactionType.LIKE);

        dto.setLikesCount(likesCount);

        // COUNT COMMENTS
        long commentsCount = commentRepository
                .countByPostId(post.getId());

        dto.setCommentsCount(commentsCount);

        // CHECK IF USER LIKED
        boolean liked = postReactionRepository
                .existsByPostIdAndUserEmailAndType(
                        post.getId(),
                        currentUsername,
                        ReactionType.LIKE);

        dto.setLikedByCurrentUser(liked);

        return dto;
    }
}

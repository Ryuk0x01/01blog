package com.blog.backend.service;

import com.blog.backend.dto.PostResponseDTO;
import com.blog.backend.entity.Post;
import com.blog.backend.entity.User;
import com.blog.backend.repository.PostRepository;
import com.blog.backend.repository.UserRepository;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    // Create post → أي user
    public PostResponseDTO createPost(String title, String content, String userEmail) {
        User author = userRepository.findByEmail(userEmail);
        Post post = new Post();
        post.setTitle(title);
        post.setContent(content);
        post.setAuthor(author);

        postRepository.save(post);

        return convertToDTO(post);
    }

    // Get all posts → public
    public List<PostResponseDTO> getAllPosts() {
        List<Post> posts = postRepository.findAll();
        List<PostResponseDTO> dtoList = posts.stream()
                .map(this::convertToDTO)
                .toList();
        return dtoList;
    }

    // Get single post → public
    public Post getPostById(Long id) {
        return postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
    }

    public PostResponseDTO getPostByIdDTO(Long id) {
        Post post = getPostById(id);
        return convertToDTO(post);
    }

    // Update post → فقط author
    public Post updatePost(Long id, String title, String content, String userEmail) {
        Post post = getPostById(id);

        // check if author
        if (!post.getAuthor().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("Only the author can update this post");
        }

        post.setTitle(title);
        post.setContent(content);
        return postRepository.save(post);
    }

    // Delete post → author أو admin
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

    private PostResponseDTO convertToDTO(Post post) {
        PostResponseDTO dto = new PostResponseDTO();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setAuthorUsername(post.getAuthor().getUsername());
        dto.setCreatedAt(post.getCreatedAt());
        dto.setUpdatedAt(post.getUpdatedAt());
        return dto;
    }
}

package com.blog.backend.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.blog.backend.entity.Post;
import com.blog.backend.entity.Report;
import com.blog.backend.entity.User;
import com.blog.backend.repository.PostRepository;
import com.blog.backend.repository.ReportRepository;
import com.blog.backend.repository.UserRepository;

@Service
public class AdminService {

    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final ReportRepository reportRepository;

    public AdminService(UserRepository userRepository,
                        PostRepository postRepository,
                        ReportRepository reportRepository) {
        this.userRepository = userRepository;
        this.postRepository = postRepository;
        this.reportRepository = reportRepository;
    }

    public List<Map<String, Object>> getAllUsers() {
        return userRepository.findAll().stream().map(u -> {
            Map<String, Object> map = new java.util.LinkedHashMap<>();
            map.put("id", u.getId());
            map.put("username", u.getUsername());
            map.put("email", u.getEmail());
            map.put("role", u.getRole());
            map.put("banned", u.isBanned());
            return map;
        }).collect(Collectors.toList());
    }

    public void toggleUserBan(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setBanned(!user.isBanned());
        userRepository.save(user);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));
        userRepository.delete(user);
    }

    public List<Map<String, Object>> getAllPosts() {
        return postRepository.findAll().stream().map(p -> {
            Map<String, Object> map = new java.util.LinkedHashMap<>();
            map.put("id", p.getId());
            map.put("title", p.getTitle());
            map.put("content", p.getContent());
            map.put("authorUsername", p.getAuthor().getUsername());
            map.put("hidden", p.isHidden());
            map.put("createdAt", p.getCreatedAt() != null ? p.getCreatedAt().toString() : null);
            return map;
        }).collect(Collectors.toList());
    }

    public void togglePostHide(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        post.setHidden(!post.isHidden());
        postRepository.save(post);
    }

    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        postRepository.delete(post);
    }

    public List<Map<String, Object>> getAllReports() {
        return reportRepository.findAll().stream().map(r -> {
            Map<String, Object> map = new java.util.LinkedHashMap<>();
            map.put("id", r.getId());
            map.put("reporterUsername", r.getReporter().getUsername());
            map.put("type", r.getType().name());
            map.put("targetId", r.getTargetId());
            map.put("description", r.getDescription());
            map.put("createdAt", r.getCreatedAt() != null ? r.getCreatedAt().toString() : null);
            return map;
        }).collect(Collectors.toList());
    }

    public void deleteReport(Long id) {
        Report report = reportRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Report not found"));
        reportRepository.delete(report);
    }

    public Map<String, Long> getStats() {
        return Map.of(
                "users", userRepository.count(),
                "posts", postRepository.count(),
                "reports", reportRepository.count()
        );
    }
}

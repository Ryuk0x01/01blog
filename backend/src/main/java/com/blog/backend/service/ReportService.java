package com.blog.backend.service;

import org.springframework.stereotype.Service;

import com.blog.backend.dto.ReportRequestDTO;
import com.blog.backend.entity.Report;
import com.blog.backend.entity.ReportType;
import com.blog.backend.entity.User;
import com.blog.backend.repository.PostRepository;
import com.blog.backend.repository.ReportRepository;
import com.blog.backend.repository.UserRepository;

@Service
public class ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;

    public ReportService(ReportRepository reportRepository,
            UserRepository userRepository,
            PostRepository postRepository) {
        this.reportRepository = reportRepository;
        this.userRepository = userRepository;
        this.postRepository = postRepository;
    }

    public void report(ReportRequestDTO dto, String email) {

        User reporter = userRepository.findByEmail(email);

        if (dto.getType() == ReportType.POST) {
            postRepository.findById(dto.getTargetId())
                    .orElseThrow(() -> new RuntimeException("Post not found"));
        }

        if (dto.getType() == ReportType.USER) {
            userRepository.findById(dto.getTargetId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
        }

        // prevent duplicate report
        if (reportRepository.existsByReporterAndTypeAndTargetId(
                reporter, dto.getType(), dto.getTargetId())) {
            throw new RuntimeException("Already reported");
        }

        Report report = new Report();
        report.setReporter(reporter);
        report.setType(dto.getType());
        report.setTargetId(dto.getTargetId());
        report.setDescription(dto.getDescription());

        reportRepository.save(report);
    }
}

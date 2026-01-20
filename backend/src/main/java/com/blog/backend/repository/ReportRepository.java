package com.blog.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.blog.backend.entity.Report;
import com.blog.backend.entity.ReportType;
import com.blog.backend.entity.User;

public interface ReportRepository extends JpaRepository<Report, Long> {

    boolean existsByReporterAndTypeAndTargetId(
            User reporter,
            ReportType type,
            Long targetId
    );
}

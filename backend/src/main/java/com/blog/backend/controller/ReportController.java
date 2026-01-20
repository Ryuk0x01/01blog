package com.blog.backend.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.blog.backend.dto.ReportRequestDTO;
import com.blog.backend.service.ReportService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @PostMapping
    public ResponseEntity<?> report(@RequestBody @Valid ReportRequestDTO dto, Authentication auth) {

        reportService.report(dto, auth.getName());

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "reported successfully"
        ));
    }
}

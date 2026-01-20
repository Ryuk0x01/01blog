package com.blog.backend.dto;

import com.blog.backend.entity.ReportType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportRequestDTO {

    @NotNull
    private ReportType type; // POST or USER

    @NotNull
    private Long targetId;

    @NotBlank
    private String description;
}

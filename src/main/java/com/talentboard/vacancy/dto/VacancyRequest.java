package com.talentboard.vacancy.dto;

import com.talentboard.vacancy.entity.VacancyStatus;
import com.talentboard.vacancy.entity.WorkMode;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

@Schema(description = "Payload for creating or updating a vacancy")
public record VacancyRequest(
        @Schema(description = "Job title", example = "Java Backend Developer")
        @NotBlank String title,

        @Schema(description = "Full job description", example = "Minimum 2 years with Spring Boot. Remote position.")
        @NotBlank String description,

        @Schema(description = "Job category or department", example = "Software Engineering")
        @NotBlank String category,

        @Schema(description = "Work mode — REMOTE, ONSITE, or HYBRID", example = "REMOTE")
        @NotNull WorkMode workMode,

        @Schema(description = "Minimum salary offered", example = "3500.00")
        BigDecimal salaryMin,

        @Schema(description = "Maximum salary offered", example = "5500.00")
        BigDecimal salaryMax,

        @Schema(description = "Vacancy status — DRAFT, OPEN, CLOSED, or CANCELLED", example = "OPEN")
        @NotNull VacancyStatus status
) {}

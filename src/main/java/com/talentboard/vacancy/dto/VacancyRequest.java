package com.talentboard.vacancy.dto;

import com.talentboard.vacancy.entity.VacancyStatus;
import com.talentboard.vacancy.entity.WorkMode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record VacancyRequest(
        @NotBlank String title,
        @NotBlank String description,
        @NotBlank String category,
        @NotNull WorkMode workMode,
        BigDecimal salaryMin,
        BigDecimal salaryMax,
        @NotNull VacancyStatus status
) {}

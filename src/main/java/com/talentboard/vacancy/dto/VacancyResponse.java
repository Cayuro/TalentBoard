package com.talentboard.vacancy.dto;

import com.talentboard.vacancy.entity.VacancyStatus;
import com.talentboard.vacancy.entity.WorkMode;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record VacancyResponse(
        Long id,
        String title,
        String description,
        String category,
        WorkMode workMode,
        BigDecimal salaryMin,
        BigDecimal salaryMax,
        LocalDate publicationDate,
        VacancyStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long recruiterId,
        String recruiterFirstName,
        String recruiterLastName
) {}

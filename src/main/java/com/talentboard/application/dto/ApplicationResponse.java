package com.talentboard.application.dto;

import com.talentboard.application.entity.ApplicationStatus;
import java.time.LocalDate;

public record ApplicationResponse(
        Long id,
        LocalDate applicationDate,
        ApplicationStatus status,
        String notes,
        Long candidateId,
        String candidateFirstName,
        String candidateLastName,
        Long vacancyId,
        String vacancyTitle
) {}

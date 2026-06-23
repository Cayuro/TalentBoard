package com.talentboard.application.dto;

import com.talentboard.application.entity.ApplicationStatus;
import jakarta.validation.constraints.NotNull;

public record ApplicationStatusUpdateRequest(
        @NotNull ApplicationStatus status,
        String notes
) {}

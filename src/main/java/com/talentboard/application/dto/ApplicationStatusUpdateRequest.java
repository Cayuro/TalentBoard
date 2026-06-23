package com.talentboard.application.dto;

import com.talentboard.application.entity.ApplicationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Payload for advancing or changing an application's status")
public record ApplicationStatusUpdateRequest(
        @Schema(
                description = "New status — APPLIED, UNDER_REVIEW, INTERVIEW_SCHEDULED, INTERVIEW_COMPLETED, TECHNICAL_TEST, OFFERED, HIRED, or REJECTED",
                example = "UNDER_REVIEW"
        )
        @NotNull ApplicationStatus status,

        @Schema(description = "Optional recruiter note about the status change", example = "Strong profile, scheduling first interview.")
        String notes
) {}

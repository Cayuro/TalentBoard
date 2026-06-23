package com.talentboard.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Payload for submitting an application to a vacancy")
public record ApplicationRequest(
        @Schema(description = "ID of the vacancy to apply to", example = "1")
        @NotNull Long vacancyId,

        @Schema(description = "Optional cover note from the candidate", example = "I have 3 years of experience with Spring Boot and PostgreSQL.")
        String notes
) {}

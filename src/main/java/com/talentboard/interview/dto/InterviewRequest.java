package com.talentboard.interview.dto;

import com.talentboard.interview.entity.InterviewType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

@Schema(description = "Payload for scheduling a new interview")
public record InterviewRequest(
        @Schema(description = "ID of the application this interview belongs to", example = "1")
        @NotNull Long applicationId,

        @Schema(description = "Interview date — must be today or in the future (yyyy-MM-dd)", example = "2026-08-15")
        @NotNull @FutureOrPresent LocalDate interviewDate,

        @Schema(description = "Interview time (HH:mm:ss)", example = "10:00:00")
        @NotNull LocalTime interviewTime,

        @Schema(description = "Interview format — PHONE, VIRTUAL, ONSITE, or TECHNICAL", example = "VIRTUAL")
        @NotNull InterviewType interviewType,

        @Schema(description = "ID of the recruiter conducting the interview", example = "2")
        @NotNull Long recruiterId,

        @Schema(description = "Optional notes or agenda for the interview", example = "Technical interview — Spring Boot, REST APIs, and databases.")
        String observations
) {}

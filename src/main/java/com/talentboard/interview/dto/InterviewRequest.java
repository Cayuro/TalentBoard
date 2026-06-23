package com.talentboard.interview.dto;

import com.talentboard.interview.entity.InterviewType;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalTime;

public record InterviewRequest(
        @NotNull Long applicationId,
        @NotNull @FutureOrPresent LocalDate interviewDate,
        @NotNull LocalTime interviewTime,
        @NotNull InterviewType interviewType,
        @NotNull Long recruiterId,
        String observations
) {}

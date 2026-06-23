package com.talentboard.application.dto;

import jakarta.validation.constraints.NotNull;

public record ApplicationRequest(
        @NotNull Long vacancyId,
        String notes
) {}

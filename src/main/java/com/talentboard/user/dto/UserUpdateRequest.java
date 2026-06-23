package com.talentboard.user.dto;

import jakarta.validation.constraints.NotBlank;

public record UserUpdateRequest(
        @NotBlank String firstName,
        @NotBlank String lastName
) {}

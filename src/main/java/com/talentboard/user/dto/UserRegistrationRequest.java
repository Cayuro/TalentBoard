package com.talentboard.user.dto;

import com.talentboard.user.entity.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Payload for registering a new user account")
public record UserRegistrationRequest(
        @Schema(description = "First name", example = "Maria")
        @NotBlank String firstName,

        @Schema(description = "Last name", example = "Gomez")
        @NotBlank String lastName,

        @Schema(description = "Unique email address", example = "maria@example.com")
        @NotBlank @Email String email,

        @Schema(description = "Password — minimum 6 characters", example = "pass123")
        @NotBlank @Size(min = 6) String password,

        @Schema(description = "User role — ADMIN, RECRUITER, or CANDIDATE", example = "CANDIDATE")
        @NotNull Role role
) {}

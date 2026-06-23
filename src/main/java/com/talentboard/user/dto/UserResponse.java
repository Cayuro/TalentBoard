package com.talentboard.user.dto;

import com.talentboard.user.entity.Role;
import java.time.LocalDateTime;

public record UserResponse(
        Long id,
        String firstName,
        String lastName,
        String email,
        Role role,
        boolean enabled,
        LocalDateTime createdAt
) {}

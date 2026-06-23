package com.talentboard.auth.dto;

import com.talentboard.user.entity.Role;

public record AuthResponse(
        String token,
        String email,
        String fullName,
        Role role
) {}

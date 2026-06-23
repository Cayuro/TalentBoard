package com.talentboard.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "TalentBoard API",
                version = "1.0",
                description = "Recruitment Management Platform REST API. "
                        + "1. Call POST /api/auth/login to obtain a JWT token. "
                        + "2. Click the Authorize button and paste the token (without the 'Bearer ' prefix). "
                        + "3. All protected endpoints will now include the token automatically.",
                contact = @Contact(name = "TalentBoard", email = "admin@talentboard.com")
        ),
        tags = {
                @Tag(name = "Authentication", description = "Register and login — no token required"),
                @Tag(name = "Vacancies",      description = "Vacancy lifecycle: create, update, change status, delete"),
                @Tag(name = "Applications",   description = "Candidate applications and status transitions"),
                @Tag(name = "Interviews",     description = "Schedule interviews and record results"),
                @Tag(name = "Users",          description = "User management — Admin only")
        }
)
@SecurityScheme(
        name = "bearerAuth",
        type = SecuritySchemeType.HTTP,
        scheme = "bearer",
        bearerFormat = "JWT",
        description = "Paste the JWT token obtained from POST /api/auth/login"
)
public class OpenApiConfig {}

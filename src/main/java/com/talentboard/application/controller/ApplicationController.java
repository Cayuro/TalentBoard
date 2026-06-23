package com.talentboard.application.controller;

import com.talentboard.application.dto.ApplicationRequest;
import com.talentboard.application.dto.ApplicationResponse;
import com.talentboard.application.dto.ApplicationStatusUpdateRequest;
import com.talentboard.application.service.ApplicationService;
import com.talentboard.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@Tag(name = "Applications", description = "Application management")
public class ApplicationController {

    private final ApplicationService applicationService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List all applications (Admin only)")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok(applicationService.findAll()));
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    @Operation(summary = "List applications for the authenticated user")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> findMine(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(applicationService.findByCandidate(user.getUsername())));
    }

    @GetMapping("/vacancy/{vacancyId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
    @Operation(summary = "List applications for a vacancy")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> findByVacancy(@PathVariable Long vacancyId) {
        return ResponseEntity.ok(ApiResponse.ok(applicationService.findByVacancy(vacancyId)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get application by ID")
    public ResponseEntity<ApiResponse<ApplicationResponse>> findById(@PathVariable Long id,
                                                                     @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(applicationService.findById(id, user.getUsername())));
    }

    @PostMapping
    @PreAuthorize("hasRole('CANDIDATE')")
    @Operation(summary = "Apply to a vacancy")
    public ResponseEntity<ApiResponse<ApplicationResponse>> apply(@Valid @RequestBody ApplicationRequest request,
                                                                  @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Application submitted", applicationService.apply(request, user.getUsername())));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
    @Operation(summary = "Update application status")
    public ResponseEntity<ApiResponse<ApplicationResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody ApplicationStatusUpdateRequest request,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(applicationService.updateStatus(id, request, user.getUsername())));
    }
}

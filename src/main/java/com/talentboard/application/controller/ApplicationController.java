package com.talentboard.application.controller;

import com.talentboard.application.dto.ApplicationRequest;
import com.talentboard.application.dto.ApplicationResponse;
import com.talentboard.application.dto.ApplicationStatusUpdateRequest;
import com.talentboard.application.service.ApplicationService;
import com.talentboard.common.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@Tag(name = "Applications")
@SecurityRequirement(name = "bearerAuth")
public class ApplicationController {

    private final ApplicationService applicationService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(
            summary = "List all applications",
            description = "Returns every application in the system. Admin only.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "All applications"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden — ADMIN role required")
            }
    )
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok(applicationService.findAll()));
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    @Operation(
            summary = "List my applications",
            description = "Candidates see their own applications. Recruiters/Admins also use this endpoint to see applications linked to their account.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Applications for the authenticated user"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> findMine(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(applicationService.findByCandidate(user.getUsername())));
    }

    @GetMapping("/vacancy/{vacancyId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
    @Operation(
            summary = "List applications for a vacancy",
            description = "Returns all applications submitted to the given vacancy. Requires ADMIN or RECRUITER role.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Applications for the vacancy"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden — CANDIDATE role cannot access this endpoint")
            }
    )
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> findByVacancy(@PathVariable Long vacancyId) {
        return ResponseEntity.ok(ApiResponse.ok(applicationService.findByVacancy(vacancyId)));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get application by ID",
            description = "Candidates can only retrieve their own applications.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Application found"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden — candidate accessing another candidate's application"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Application not found")
            }
    )
    public ResponseEntity<ApiResponse<ApplicationResponse>> findById(@PathVariable Long id,
                                                                     @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(applicationService.findById(id, user.getUsername())));
    }

    @PostMapping
    @PreAuthorize("hasRole('CANDIDATE')")
    @Operation(
            summary = "Apply to a vacancy",
            description = "Submits an application. Business rules: vacancy must be OPEN; candidate cannot apply twice to the same vacancy.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Application submitted successfully"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error — vacancyId is required"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden — CANDIDATE role required"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "Conflict — vacancy is not OPEN, or candidate already applied")
            }
    )
    public ResponseEntity<ApiResponse<ApplicationResponse>> apply(@Valid @RequestBody ApplicationRequest request,
                                                                  @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Application submitted", applicationService.apply(request, user.getUsername())));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
    @Operation(
            summary = "Update application status",
            description = "Advances or changes the application status. "
                    + "Allowed statuses: APPLIED, UNDER_REVIEW, INTERVIEW_SCHEDULED, INTERVIEW_COMPLETED, TECHNICAL_TEST, OFFERED, HIRED, REJECTED.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Status updated"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error — status is required"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden — CANDIDATE role cannot update status"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Application not found")
            }
    )
    public ResponseEntity<ApiResponse<ApplicationResponse>> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody ApplicationStatusUpdateRequest request,
            @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(applicationService.updateStatus(id, request, user.getUsername())));
    }
}

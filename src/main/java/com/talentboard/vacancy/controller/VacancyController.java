package com.talentboard.vacancy.controller;

import com.talentboard.common.response.ApiResponse;
import com.talentboard.vacancy.dto.VacancyRequest;
import com.talentboard.vacancy.dto.VacancyResponse;
import com.talentboard.vacancy.entity.VacancyStatus;
import com.talentboard.vacancy.service.VacancyService;
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
@RequestMapping("/api/vacancies")
@RequiredArgsConstructor
@Tag(name = "Vacancies")
public class VacancyController {

    private final VacancyService vacancyService;

    @GetMapping("/public")
    @Operation(
            summary = "List all open vacancies (public)",
            description = "Returns all vacancies with status OPEN. No authentication required.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "List of open vacancies")
            }
    )
    public ResponseEntity<ApiResponse<List<VacancyResponse>>> findOpen() {
        return ResponseEntity.ok(ApiResponse.ok(vacancyService.findOpen()));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "List all vacancies",
            description = "Returns all vacancies regardless of status. Requires ADMIN or RECRUITER role.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "List of all vacancies"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized — missing or invalid token"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden — CANDIDATE role cannot access this endpoint")
            }
    )
    public ResponseEntity<ApiResponse<List<VacancyResponse>>> findAll() {
        return ResponseEntity.ok(ApiResponse.ok(vacancyService.findAll()));
    }

    @GetMapping("/my")
    @PreAuthorize("hasRole('RECRUITER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "List vacancies owned by the authenticated recruiter",
            description = "Returns only the vacancies created by the currently authenticated recruiter.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Recruiter's vacancies"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized — missing or invalid token"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden — RECRUITER role required")
            }
    )
    public ResponseEntity<ApiResponse<List<VacancyResponse>>> findMine(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(vacancyService.findByRecruiterEmail(user.getUsername())));
    }

    @GetMapping("/{id}")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Get vacancy by ID",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Vacancy found"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized — missing or invalid token"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Vacancy not found")
            }
    )
    public ResponseEntity<ApiResponse<VacancyResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(vacancyService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Create a new vacancy",
            description = "Creates a vacancy assigned to the authenticated recruiter. Requires ADMIN or RECRUITER role.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Vacancy created"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error — missing required fields"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized — missing or invalid token"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden — CANDIDATE role cannot create vacancies")
            }
    )
    public ResponseEntity<ApiResponse<VacancyResponse>> create(@Valid @RequestBody VacancyRequest request,
                                                               @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Vacancy created", vacancyService.create(request, user.getUsername())));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Update a vacancy",
            description = "Recruiters can only update their own vacancies. Admins can update any vacancy.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Vacancy updated"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden — recruiter trying to edit another's vacancy"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Vacancy not found")
            }
    )
    public ResponseEntity<ApiResponse<VacancyResponse>> update(@PathVariable Long id,
                                                               @Valid @RequestBody VacancyRequest request,
                                                               @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(vacancyService.update(id, request, user.getUsername())));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Change vacancy status",
            description = "Allowed values: DRAFT, OPEN, CLOSED, CANCELLED. Closing a vacancy prevents new applications.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Status updated"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Vacancy not found")
            }
    )
    public ResponseEntity<ApiResponse<VacancyResponse>> changeStatus(@PathVariable Long id,
                                                                     @RequestParam VacancyStatus status,
                                                                     @AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(vacancyService.changeStatus(id, status, user.getUsername())));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(
            summary = "Delete a vacancy",
            description = "Recruiters can only delete their own vacancies. Admins can delete any vacancy.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Vacancy deleted"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Vacancy not found")
            }
    )
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id,
                                                    @AuthenticationPrincipal UserDetails user) {
        vacancyService.delete(id, user.getUsername());
        return ResponseEntity.ok(ApiResponse.ok("Vacancy deleted", null));
    }
}

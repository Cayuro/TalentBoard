package com.talentboard.interview.controller;

import com.talentboard.common.response.ApiResponse;
import com.talentboard.interview.dto.InterviewRequest;
import com.talentboard.interview.dto.InterviewResponse;
import com.talentboard.interview.entity.InterviewResult;
import com.talentboard.interview.service.InterviewService;
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
@RequestMapping("/api/interviews")
@RequiredArgsConstructor
@Tag(name = "Interviews")
@SecurityRequirement(name = "bearerAuth")
public class InterviewController {

    private final InterviewService interviewService;

    @GetMapping("/application/{applicationId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
    @Operation(
            summary = "List interviews for an application",
            description = "Returns all interviews linked to the given application. Requires ADMIN or RECRUITER role.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Interviews for the application"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden — CANDIDATE role cannot access this endpoint")
            }
    )
    public ResponseEntity<ApiResponse<List<InterviewResponse>>> findByApplication(@PathVariable Long applicationId) {
        return ResponseEntity.ok(ApiResponse.ok(interviewService.findByApplication(applicationId)));
    }

    @GetMapping("/my")
    @PreAuthorize("hasAnyRole('CANDIDATE', 'RECRUITER', 'ADMIN')")
    @Operation(
            summary = "List my interviews",
            description = "Candidates see interviews linked to their applications. Recruiters see interviews they are assigned to.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Interviews for the authenticated user"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized")
            }
    )
    public ResponseEntity<ApiResponse<List<InterviewResponse>>> findMine(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(interviewService.findByCandidate(user.getUsername())));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get interview by ID",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Interview found"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Interview not found")
            }
    )
    public ResponseEntity<ApiResponse<InterviewResponse>> findById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(interviewService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
    @Operation(
            summary = "Schedule an interview",
            description = "Creates a new interview for an application. "
                    + "The interviewDate must be today or in the future (@FutureOrPresent). "
                    + "Automatically advances the application status to INTERVIEW_SCHEDULED.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Interview scheduled — application status advanced to INTERVIEW_SCHEDULED"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Validation error — past date or missing required fields"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden — CANDIDATE role cannot schedule interviews"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Application or recruiter not found")
            }
    )
    public ResponseEntity<ApiResponse<InterviewResponse>> schedule(@Valid @RequestBody InterviewRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Interview scheduled", interviewService.schedule(request)));
    }

    @PatchMapping("/{id}/result")
    @PreAuthorize("hasAnyRole('ADMIN', 'RECRUITER')")
    @Operation(
            summary = "Register interview result",
            description = "Records the outcome of an interview. "
                    + "Allowed values for result: PENDING, PASSED, FAILED, NO_SHOW. "
                    + "When result is PASSED, FAILED, or NO_SHOW, the application status advances to INTERVIEW_COMPLETED.",
            responses = {
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Result registered — application status advanced to INTERVIEW_COMPLETED"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Unauthorized"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Forbidden — CANDIDATE role cannot record results"),
                    @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Interview not found")
            }
    )
    public ResponseEntity<ApiResponse<InterviewResponse>> updateResult(
            @PathVariable Long id,
            @RequestParam InterviewResult result,
            @RequestParam(required = false) String observations) {
        return ResponseEntity.ok(ApiResponse.ok(interviewService.updateResult(id, result, observations)));
    }
}

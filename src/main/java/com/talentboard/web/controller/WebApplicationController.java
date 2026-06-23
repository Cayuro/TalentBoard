package com.talentboard.web.controller;

import com.talentboard.application.dto.ApplicationRequest;
import com.talentboard.application.dto.ApplicationStatusUpdateRequest;
import com.talentboard.application.entity.ApplicationStatus;
import com.talentboard.application.service.ApplicationService;
import com.talentboard.common.exception.BusinessRuleException;
import com.talentboard.common.exception.UnauthorizedException;
import com.talentboard.interview.entity.InterviewResult;
import com.talentboard.interview.entity.InterviewType;
import com.talentboard.interview.service.InterviewService;
import com.talentboard.user.entity.Role;
import com.talentboard.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/applications")
@RequiredArgsConstructor
public class WebApplicationController {

    private final ApplicationService applicationService;
    private final UserService userService;
    private final InterviewService interviewService;

    @GetMapping
    public String list(Model model, @AuthenticationPrincipal UserDetails user) {
        var currentUser = userService.findByEmail(user.getUsername());
        model.addAttribute("currentUser", currentUser);

        if (currentUser.role() == Role.ADMIN) {
            model.addAttribute("applications", applicationService.findAll());
            model.addAttribute("pageTitle", "All Applications");
        } else if (currentUser.role() == Role.RECRUITER) {
            model.addAttribute("applications", applicationService.findByRecruiter(user.getUsername()));
            model.addAttribute("pageTitle", "Applications for My Vacancies");
        } else {
            model.addAttribute("applications", applicationService.findByCandidate(user.getUsername()));
            model.addAttribute("pageTitle", "My Applications");
        }
        return "applications/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model,
                         @AuthenticationPrincipal UserDetails user) {
        model.addAttribute("currentUser", userService.findByEmail(user.getUsername()));
        model.addAttribute("app", applicationService.findById(id, user.getUsername()));
        model.addAttribute("interviews", interviewService.findByApplication(id));
        model.addAttribute("interviewTypes", InterviewType.values());
        model.addAttribute("interviewResults", InterviewResult.values());
        model.addAttribute("applicationStatuses", ApplicationStatus.values());
        return "applications/detail";
    }

    @PostMapping
    public String apply(@ModelAttribute ApplicationRequest request,
                        @AuthenticationPrincipal UserDetails user,
                        RedirectAttributes redirectAttributes) {
        try {
            applicationService.apply(request, user.getUsername());
            redirectAttributes.addFlashAttribute("successMessage", "Application submitted successfully");
        } catch (BusinessRuleException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/vacancies/" + request.vacancyId();
    }

    @PostMapping("/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam ApplicationStatus status,
                               @RequestParam(required = false) String notes,
                               @AuthenticationPrincipal UserDetails user,
                               RedirectAttributes redirectAttributes) {
        try {
            applicationService.updateStatus(id, new ApplicationStatusUpdateRequest(status, notes), user.getUsername());
            redirectAttributes.addFlashAttribute("successMessage", "Status updated successfully");
        } catch (BusinessRuleException | UnauthorizedException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/applications/" + id;
    }
}

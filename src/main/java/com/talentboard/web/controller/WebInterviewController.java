package com.talentboard.web.controller;

import com.talentboard.application.service.ApplicationService;
import com.talentboard.common.exception.BusinessRuleException;
import com.talentboard.interview.dto.InterviewRequest;
import com.talentboard.interview.entity.InterviewResult;
import com.talentboard.interview.entity.InterviewType;
import com.talentboard.interview.service.InterviewService;
import com.talentboard.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalTime;

@Controller
@RequestMapping("/interviews")
@RequiredArgsConstructor
public class WebInterviewController {

    private final InterviewService interviewService;
    private final ApplicationService applicationService;
    private final UserService userService;

    @GetMapping
    public String list(Model model, @AuthenticationPrincipal UserDetails user) {
        model.addAttribute("currentUser", userService.findByEmail(user.getUsername()));
        model.addAttribute("interviews", interviewService.findByRecruiter(user.getUsername()));
        return "interviews/list";
    }

    @GetMapping("/application/{applicationId}/new")
    public String scheduleForm(@PathVariable Long applicationId, Model model,
                               @AuthenticationPrincipal UserDetails user) {
        model.addAttribute("currentUser", userService.findByEmail(user.getUsername()));
        model.addAttribute("app", applicationService.findById(applicationId, user.getUsername()));
        model.addAttribute("interviewTypes", InterviewType.values());
        return "interviews/schedule";
    }

    @PostMapping("/application/{applicationId}")
    public String schedule(@PathVariable Long applicationId,
                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate interviewDate,
                           @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime interviewTime,
                           @RequestParam InterviewType interviewType,
                           @RequestParam(required = false) String observations,
                           @AuthenticationPrincipal UserDetails user,
                           RedirectAttributes redirectAttributes) {
        try {
            Long recruiterId = userService.findByEmail(user.getUsername()).id();
            InterviewRequest request = new InterviewRequest(
                    applicationId, interviewDate, interviewTime, interviewType, recruiterId, observations
            );
            interviewService.schedule(request);
            redirectAttributes.addFlashAttribute("successMessage", "Interview scheduled successfully");
        } catch (BusinessRuleException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/applications/" + applicationId;
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model,
                         @AuthenticationPrincipal UserDetails user) {
        model.addAttribute("currentUser", userService.findByEmail(user.getUsername()));
        model.addAttribute("interview", interviewService.findById(id));
        model.addAttribute("interviewResults", InterviewResult.values());
        return "interviews/detail";
    }

    @PostMapping("/{id}/result")
    public String updateResult(@PathVariable Long id,
                               @RequestParam InterviewResult result,
                               @RequestParam(required = false) String observations,
                               RedirectAttributes redirectAttributes) {
        try {
            interviewService.updateResult(id, result, observations);
            redirectAttributes.addFlashAttribute("successMessage", "Interview result updated");
        } catch (BusinessRuleException ex) {
            redirectAttributes.addFlashAttribute("errorMessage", ex.getMessage());
        }
        return "redirect:/interviews/" + id;
    }
}

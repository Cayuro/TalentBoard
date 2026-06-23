package com.talentboard.web.controller;

import com.talentboard.application.service.ApplicationService;
import com.talentboard.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/applications")
@RequiredArgsConstructor
public class WebApplicationController {

    private final ApplicationService applicationService;
    private final UserService userService;

    @GetMapping
    public String list(Model model, @AuthenticationPrincipal UserDetails user) {
        model.addAttribute("currentUser", userService.findByEmail(user.getUsername()));
        model.addAttribute("applications", applicationService.findByCandidate(user.getUsername()));
        return "applications/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model,
                         @AuthenticationPrincipal UserDetails user) {
        model.addAttribute("currentUser", userService.findByEmail(user.getUsername()));
        model.addAttribute("application", applicationService.findById(id, user.getUsername()));
        return "applications/detail";
    }
}

package com.talentboard.web.controller;

import com.talentboard.auth.service.AuthService;
import com.talentboard.user.dto.UserRegistrationRequest;
import com.talentboard.user.entity.Role;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class WebAuthController {

    private final AuthService authService;

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("roles", Role.values());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute UserRegistrationRequest request,
                           BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("error", "Please check the form fields and try again.");
            model.addAttribute("roles", Role.values());
            return "auth/register";
        }
        try {
            authService.register(request);
            return "redirect:/auth/login?registered=true";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("roles", Role.values());
            return "auth/register";
        }
    }
}

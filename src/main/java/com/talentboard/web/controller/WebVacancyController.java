package com.talentboard.web.controller;

import com.talentboard.application.service.ApplicationService;
import com.talentboard.common.exception.BusinessRuleException;
import com.talentboard.user.service.UserService;
import com.talentboard.vacancy.dto.VacancyRequest;
import com.talentboard.vacancy.entity.VacancyStatus;
import com.talentboard.vacancy.entity.WorkMode;
import com.talentboard.vacancy.service.VacancyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/vacancies")
@RequiredArgsConstructor
public class WebVacancyController {

    private final VacancyService vacancyService;
    private final ApplicationService applicationService;
    private final UserService userService;

    @GetMapping
    public String list(Model model, @AuthenticationPrincipal UserDetails user) {
        model.addAttribute("currentUser", userService.findByEmail(user.getUsername()));
        model.addAttribute("vacancies", vacancyService.findAll());
        return "vacancies/list";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model,
                         @AuthenticationPrincipal UserDetails user) {
        model.addAttribute("currentUser", userService.findByEmail(user.getUsername()));
        model.addAttribute("vacancy", vacancyService.findById(id));
        model.addAttribute("applications", applicationService.findByVacancy(id));
        return "vacancies/detail";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        model.addAttribute("vacancyRequest", new VacancyRequest(null, null, null, null, null, null, VacancyStatus.DRAFT));
        model.addAttribute("workModes", WorkMode.values());
        model.addAttribute("statuses", VacancyStatus.values());
        return "vacancies/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute VacancyRequest vacancyRequest,
                         BindingResult result, Model model,
                         @AuthenticationPrincipal UserDetails user) {
        if (result.hasErrors()) {
            model.addAttribute("errorMessage", "Please review the highlighted vacancy fields.");
            model.addAttribute("workModes", WorkMode.values());
            model.addAttribute("statuses", VacancyStatus.values());
            return "vacancies/form";
        }
        vacancyService.create(vacancyRequest, user.getUsername());
        return "redirect:/vacancies";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model,
                           @AuthenticationPrincipal UserDetails user) {
        model.addAttribute("currentUser", userService.findByEmail(user.getUsername()));
        model.addAttribute("vacancy", vacancyService.findById(id));
        model.addAttribute("workModes", WorkMode.values());
        model.addAttribute("statuses", VacancyStatus.values());
        return "vacancies/edit";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute VacancyRequest vacancyRequest,
                         BindingResult result,
                         Model model,
                         @AuthenticationPrincipal UserDetails user,
                         RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("errorMessage", "Please review the highlighted vacancy fields.");
            model.addAttribute("vacancy", vacancyService.findById(id));
            model.addAttribute("workModes", WorkMode.values());
            model.addAttribute("statuses", VacancyStatus.values());
            return "vacancies/edit";
        }
        vacancyService.update(id, vacancyRequest, user.getUsername());
        redirectAttributes.addFlashAttribute("successMessage", "Vacancy updated successfully");
        return "redirect:/vacancies/" + id;
    }
}

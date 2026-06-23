package com.talentboard.application.service;

import com.talentboard.application.dto.ApplicationRequest;
import com.talentboard.application.dto.ApplicationResponse;
import com.talentboard.application.dto.ApplicationStatusUpdateRequest;
import com.talentboard.application.entity.Application;
import com.talentboard.application.entity.ApplicationStatus;
import com.talentboard.application.repository.ApplicationRepository;
import com.talentboard.common.exception.BusinessRuleException;
import com.talentboard.common.exception.ResourceNotFoundException;
import com.talentboard.common.exception.UnauthorizedException;
import com.talentboard.common.mapper.ApplicationMapper;
import com.talentboard.user.entity.Role;
import com.talentboard.user.entity.User;
import com.talentboard.user.service.UserService;
import com.talentboard.vacancy.entity.Vacancy;
import com.talentboard.vacancy.entity.VacancyStatus;
import com.talentboard.vacancy.service.VacancyService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final ApplicationMapper applicationMapper;
    private final UserService userService;
    private final VacancyService vacancyService;

    public List<ApplicationResponse> findAll() {
        return applicationRepository.findAll().stream().map(applicationMapper::toResponse).toList();
    }

    public List<ApplicationResponse> findByCandidate(String candidateEmail) {
        Long candidateId = userService.findByEmail(candidateEmail).id();
        return applicationRepository.findByCandidateId(candidateId)
                .stream().map(applicationMapper::toResponse).toList();
    }

    public List<ApplicationResponse> findByVacancy(Long vacancyId) {
        return applicationRepository.findByVacancyId(vacancyId)
                .stream().map(applicationMapper::toResponse).toList();
    }

    public List<ApplicationResponse> findByRecruiter(String recruiterEmail) {
        Long recruiterId = userService.findByEmail(recruiterEmail).id();
        return applicationRepository.findByVacancyRecruiterId(recruiterId)
                .stream().map(applicationMapper::toResponse).toList();
    }

    public ApplicationResponse findById(Long id, String requesterEmail) {
        Application app = getById(id);
        checkAccess(app, requesterEmail);
        return applicationMapper.toResponse(app);
    }

    @Transactional
    public ApplicationResponse apply(ApplicationRequest request, String candidateEmail) {
        User candidate = userService.getById(userService.findByEmail(candidateEmail).id());
        Vacancy vacancy = vacancyService.getById(request.vacancyId());

        if (vacancy.getStatus() != VacancyStatus.OPEN) {
            throw new BusinessRuleException("Cannot apply to a vacancy that is not OPEN");
        }
        if (applicationRepository.existsByCandidateIdAndVacancyId(candidate.getId(), vacancy.getId())) {
            throw new BusinessRuleException("You have already applied to this vacancy");
        }

        Application application = applicationMapper.toEntity(request);
        application.setCandidate(candidate);
        application.setVacancy(vacancy);
        application.setStatus(ApplicationStatus.APPLIED);
        return applicationMapper.toResponse(applicationRepository.save(application));
    }

    @Transactional
    public ApplicationResponse updateStatus(Long id, ApplicationStatusUpdateRequest request, String requesterEmail) {
        Application app = getById(id);
        User requester = userService.getById(userService.findByEmail(requesterEmail).id());
        if (requester.getRole() == Role.CANDIDATE) {
            throw new UnauthorizedException("Candidates cannot update application status");
        }
        app.setStatus(request.status());
        if (request.notes() != null) app.setNotes(request.notes());
        return applicationMapper.toResponse(applicationRepository.save(app));
    }

    public Application getById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found: " + id));
    }

    private void checkAccess(Application app, String requesterEmail) {
        User requester = userService.getById(userService.findByEmail(requesterEmail).id());
        if (requester.getRole() == Role.ADMIN) return;
        if (requester.getRole() == Role.RECRUITER) return;
        if (!app.getCandidate().getEmail().equals(requesterEmail)) {
            throw new UnauthorizedException("Access denied");
        }
    }
}

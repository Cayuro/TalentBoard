package com.talentboard.vacancy.service;

import com.talentboard.common.exception.ResourceNotFoundException;
import com.talentboard.common.exception.UnauthorizedException;
import com.talentboard.common.mapper.VacancyMapper;
import com.talentboard.user.entity.Role;
import com.talentboard.user.entity.User;
import com.talentboard.user.service.UserService;
import com.talentboard.vacancy.dto.VacancyRequest;
import com.talentboard.vacancy.dto.VacancyResponse;
import com.talentboard.vacancy.entity.Vacancy;
import com.talentboard.vacancy.entity.VacancyStatus;
import com.talentboard.vacancy.repository.VacancyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VacancyService {

    private final VacancyRepository vacancyRepository;
    private final VacancyMapper vacancyMapper;
    private final UserService userService;

    public List<VacancyResponse> findAll() {
        return vacancyRepository.findAll().stream().map(vacancyMapper::toResponse).toList();
    }

    public List<VacancyResponse> findOpen() {
        return vacancyRepository.findByStatus(VacancyStatus.OPEN)
                .stream().map(vacancyMapper::toResponse).toList();
    }

    public List<VacancyResponse> findByRecruiter(Long recruiterId) {
        return vacancyRepository.findByRecruiterId(recruiterId)
                .stream().map(vacancyMapper::toResponse).toList();
    }

    public List<VacancyResponse> findByRecruiterEmail(String email) {
        Long recruiterId = userService.findByEmail(email).id();
        return vacancyRepository.findByRecruiterId(recruiterId)
                .stream().map(vacancyMapper::toResponse).toList();
    }

    public VacancyResponse findById(Long id) {
        return vacancyMapper.toResponse(getById(id));
    }

    @Transactional
    public VacancyResponse create(VacancyRequest request, String recruiterEmail) {
        User recruiter = userService.getById(getUserIdByEmail(recruiterEmail));
        Vacancy vacancy = vacancyMapper.toEntity(request);
        vacancy.setRecruiter(recruiter);
        vacancy.setPublicationDate(LocalDate.now());
        return vacancyMapper.toResponse(vacancyRepository.save(vacancy));
    }

    @Transactional
    public VacancyResponse update(Long id, VacancyRequest request, String requesterEmail) {
        Vacancy vacancy = getById(id);
        checkOwnership(vacancy, requesterEmail);
        vacancyMapper.updateEntity(request, vacancy);
        return vacancyMapper.toResponse(vacancyRepository.save(vacancy));
    }

    @Transactional
    public VacancyResponse changeStatus(Long id, VacancyStatus status, String requesterEmail) {
        Vacancy vacancy = getById(id);
        checkOwnership(vacancy, requesterEmail);
        vacancy.setStatus(status);
        return vacancyMapper.toResponse(vacancyRepository.save(vacancy));
    }

    @Transactional
    public void delete(Long id, String requesterEmail) {
        Vacancy vacancy = getById(id);
        checkOwnership(vacancy, requesterEmail);
        vacancyRepository.delete(vacancy);
    }

    public Vacancy getById(Long id) {
        return vacancyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vacancy not found: " + id));
    }

    private void checkOwnership(Vacancy vacancy, String requesterEmail) {
        User requester = userService.getById(getUserIdByEmail(requesterEmail));
        if (requester.getRole() == Role.ADMIN) return;
        if (!vacancy.getRecruiter().getEmail().equals(requesterEmail)) {
            throw new UnauthorizedException("You do not own this vacancy");
        }
    }

    private Long getUserIdByEmail(String email) {
        return userService.findByEmail(email).id();
    }
}

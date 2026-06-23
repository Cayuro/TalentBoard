package com.talentboard.interview.service;

import com.talentboard.application.entity.Application;
import com.talentboard.application.entity.ApplicationStatus;
import com.talentboard.application.repository.ApplicationRepository;
import com.talentboard.common.exception.BusinessRuleException;
import com.talentboard.common.exception.ResourceNotFoundException;
import com.talentboard.common.mapper.InterviewMapper;
import com.talentboard.interview.dto.InterviewRequest;
import com.talentboard.interview.dto.InterviewResponse;
import com.talentboard.interview.entity.Interview;
import com.talentboard.interview.entity.InterviewResult;
import com.talentboard.interview.repository.InterviewRepository;
import com.talentboard.user.entity.User;
import com.talentboard.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final InterviewMapper interviewMapper;
    private final ApplicationRepository applicationRepository;
    private final UserService userService;

    public List<InterviewResponse> findByApplication(Long applicationId) {
        return interviewRepository.findByApplicationId(applicationId)
                .stream().map(interviewMapper::toResponse).toList();
    }

    public List<InterviewResponse> findByCandidate(String candidateEmail) {
        Long candidateId = userService.findByEmail(candidateEmail).id();
        return interviewRepository.findByApplicationCandidateId(candidateId)
                .stream().map(interviewMapper::toResponse).toList();
    }

    public List<InterviewResponse> findByRecruiter(String recruiterEmail) {
        Long recruiterId = userService.findByEmail(recruiterEmail).id();
        return interviewRepository.findByRecruiterId(recruiterId)
                .stream().map(interviewMapper::toResponse).toList();
    }

    public InterviewResponse findById(Long id) {
        return interviewMapper.toResponse(getById(id));
    }

    @Transactional
    public InterviewResponse schedule(InterviewRequest request) {
        if (request.interviewDate().isBefore(LocalDate.now())) {
            throw new BusinessRuleException("Interview date cannot be in the past");
        }

        Application application = applicationRepository.findById(request.applicationId())
                .orElseThrow(() -> new ResourceNotFoundException("Application not found: " + request.applicationId()));

        User recruiter = userService.getById(request.recruiterId());

        Interview interview = interviewMapper.toEntity(request);
        interview.setApplication(application);
        interview.setRecruiter(recruiter);
        interview.setResult(InterviewResult.PENDING);

        application.setStatus(ApplicationStatus.INTERVIEW_SCHEDULED);
        applicationRepository.save(application);

        return interviewMapper.toResponse(interviewRepository.save(interview));
    }

    @Transactional
    public InterviewResponse updateResult(Long id, InterviewResult result, String observations) {
        Interview interview = getById(id);
        interview.setResult(result);
        if (observations != null) interview.setObservations(observations);

        if (result == InterviewResult.PASSED || result == InterviewResult.FAILED
                || result == InterviewResult.NO_SHOW) {
            interview.getApplication().setStatus(ApplicationStatus.INTERVIEW_COMPLETED);
            applicationRepository.save(interview.getApplication());
        }
        return interviewMapper.toResponse(interviewRepository.save(interview));
    }

    public Interview getById(Long id) {
        return interviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Interview not found: " + id));
    }
}

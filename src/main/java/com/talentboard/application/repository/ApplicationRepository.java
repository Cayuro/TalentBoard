package com.talentboard.application.repository;

import com.talentboard.application.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ApplicationRepository extends JpaRepository<Application, Long> {
    boolean existsByCandidateIdAndVacancyId(Long candidateId, Long vacancyId);
    List<Application> findByCandidateId(Long candidateId);
    List<Application> findByVacancyId(Long vacancyId);
    List<Application> findByVacancyRecruiterId(Long recruiterId);
    Optional<Application> findByIdAndCandidateId(Long id, Long candidateId);
}

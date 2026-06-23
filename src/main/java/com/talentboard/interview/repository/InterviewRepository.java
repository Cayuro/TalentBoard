package com.talentboard.interview.repository;

import com.talentboard.interview.entity.Interview;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InterviewRepository extends JpaRepository<Interview, Long> {
    List<Interview> findByApplicationId(Long applicationId);
    List<Interview> findByRecruiterId(Long recruiterId);
    List<Interview> findByApplicationCandidateId(Long candidateId);
}

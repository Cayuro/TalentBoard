package com.talentboard.vacancy.repository;

import com.talentboard.vacancy.entity.Vacancy;
import com.talentboard.vacancy.entity.VacancyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface VacancyRepository extends JpaRepository<Vacancy, Long> {
    List<Vacancy> findByStatus(VacancyStatus status);
    List<Vacancy> findByRecruiterId(Long recruiterId);
    List<Vacancy> findByRecruiterIdAndStatus(Long recruiterId, VacancyStatus status);
}

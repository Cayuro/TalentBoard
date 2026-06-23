package com.talentboard.common.mapper;

import com.talentboard.vacancy.dto.VacancyRequest;
import com.talentboard.vacancy.dto.VacancyResponse;
import com.talentboard.vacancy.entity.Vacancy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface VacancyMapper {

    @Mapping(source = "recruiter.id", target = "recruiterId")
    @Mapping(source = "recruiter.firstName", target = "recruiterFirstName")
    @Mapping(source = "recruiter.lastName", target = "recruiterLastName")
    VacancyResponse toResponse(Vacancy vacancy);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "recruiter", ignore = true)
    @Mapping(target = "applications", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "publicationDate", ignore = true)
    Vacancy toEntity(VacancyRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "recruiter", ignore = true)
    @Mapping(target = "applications", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "publicationDate", ignore = true)
    void updateEntity(VacancyRequest request, @MappingTarget Vacancy vacancy);
}

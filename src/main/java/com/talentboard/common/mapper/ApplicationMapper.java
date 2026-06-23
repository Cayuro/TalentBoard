package com.talentboard.common.mapper;

import com.talentboard.application.dto.ApplicationRequest;
import com.talentboard.application.dto.ApplicationResponse;
import com.talentboard.application.entity.Application;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ApplicationMapper {

    @Mapping(source = "candidate.id", target = "candidateId")
    @Mapping(source = "candidate.firstName", target = "candidateFirstName")
    @Mapping(source = "candidate.lastName", target = "candidateLastName")
    @Mapping(source = "vacancy.id", target = "vacancyId")
    @Mapping(source = "vacancy.title", target = "vacancyTitle")
    ApplicationResponse toResponse(Application application);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "candidate", ignore = true)
    @Mapping(target = "vacancy", ignore = true)
    @Mapping(target = "interviews", ignore = true)
    @Mapping(target = "applicationDate", ignore = true)
    @Mapping(target = "status", ignore = true)
    Application toEntity(ApplicationRequest request);
}

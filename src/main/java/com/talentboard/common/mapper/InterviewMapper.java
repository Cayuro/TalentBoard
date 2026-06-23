package com.talentboard.common.mapper;

import com.talentboard.interview.dto.InterviewRequest;
import com.talentboard.interview.dto.InterviewResponse;
import com.talentboard.interview.entity.Interview;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface InterviewMapper {

    @Mapping(source = "application.id", target = "applicationId")
    @Mapping(source = "application.candidate.id", target = "candidateId")
    @Mapping(source = "application.candidate.firstName", target = "candidateFirstName")
    @Mapping(source = "application.candidate.lastName", target = "candidateLastName")
    @Mapping(source = "recruiter.id", target = "recruiterId")
    @Mapping(source = "recruiter.firstName", target = "recruiterFirstName")
    @Mapping(source = "recruiter.lastName", target = "recruiterLastName")
    InterviewResponse toResponse(Interview interview);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "application", ignore = true)
    @Mapping(target = "recruiter", ignore = true)
    @Mapping(target = "result", ignore = true)
    Interview toEntity(InterviewRequest request);
}

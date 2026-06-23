package com.talentboard.interview.dto;

import com.talentboard.interview.entity.InterviewResult;
import com.talentboard.interview.entity.InterviewType;
import java.time.LocalDate;
import java.time.LocalTime;

public record InterviewResponse(
        Long id,
        LocalDate interviewDate,
        LocalTime interviewTime,
        InterviewType interviewType,
        InterviewResult result,
        String observations,
        Long applicationId,
        Long candidateId,
        String candidateFirstName,
        String candidateLastName,
        Long recruiterId,
        String recruiterFirstName,
        String recruiterLastName
) {}

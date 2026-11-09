package com.skill_gate.mapper;

import com.skill_gate.dto.AnswerOptionDto;
import com.skill_gate.entity.DbAnswerOption;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AnswerOptionMapper {
    AnswerOptionDto dbToDto(DbAnswerOption answerOption);
}

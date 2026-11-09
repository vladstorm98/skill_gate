package com.skill_gate.mapper;

import com.skill_gate.dto.QuestionDto;
import com.skill_gate.entity.DbQuestion;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface QuestionMapper {
    QuestionDto dbToDto(DbQuestion question);
}

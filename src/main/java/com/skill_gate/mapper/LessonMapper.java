package com.skill_gate.mapper;

import com.skill_gate.dto.LessonDraftDto;
import com.skill_gate.entity.DbLesson;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface LessonMapper {
    LessonDraftDto dbToDto(DbLesson lesson);
}


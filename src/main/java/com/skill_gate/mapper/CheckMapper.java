package com.skill_gate.mapper;

import com.skill_gate.dto.CheckDto;
import com.skill_gate.entity.DbCheck;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CheckMapper {
    CheckDto dbToDto(DbCheck check);
}



package com.skill_gate.mapper;

import com.skill_gate.dto.PositionDto;
import com.skill_gate.entity.DbPosition;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PositionMapper {
    PositionDto dbToDto(DbPosition position);
}

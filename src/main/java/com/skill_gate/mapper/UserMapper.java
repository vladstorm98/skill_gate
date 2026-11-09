package com.skill_gate.mapper;

import com.skill_gate.dto.UserDto;
import com.skill_gate.entity.DbUser;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
    @Mapping(target = "positionName", source = "position.name")
    UserDto dbToDto(DbUser user);
}

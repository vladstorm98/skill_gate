package com.skill_gate.dto;

import com.skill_gate.entity.UserRole;
import com.skill_gate.entity.UserStatus;

public record EmployeeFilterDto(
        UserStatus status,
        UserRole role,
        Long positionId
) {
    public static EmployeeFilterDto empty() {
        return new EmployeeFilterDto(null, null, null);
    }

    public EmployeeFilterDto withStatus(UserStatus status) {
        return new EmployeeFilterDto(status, role, positionId);
    }

    public EmployeeFilterDto withRole(UserRole role) {
        return new EmployeeFilterDto(status, role, positionId);
    }

    public EmployeeFilterDto withPositionId(Long positionId) {
        return new EmployeeFilterDto(status, role, positionId);
    }
}

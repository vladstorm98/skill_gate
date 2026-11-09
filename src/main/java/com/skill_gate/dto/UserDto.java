package com.skill_gate.dto;

import com.skill_gate.entity.UserRole;
import com.skill_gate.entity.UserState;
import com.skill_gate.entity.UserStatus;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record UserDto(
        Long id,
        Long telegramId,
        String fullName,
        String phone,
        String positionName,
        UserRole role,
        UserStatus status,
        UserState currentState,
        Long currentLessonNumber,
        BigDecimal onboardingProgress,
        ZonedDateTime createdAt,
        ZonedDateTime updatedAt
) implements Dto, Serializable {}

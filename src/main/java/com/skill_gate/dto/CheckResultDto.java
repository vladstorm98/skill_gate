package com.skill_gate.dto;

import java.io.Serializable;
import java.math.BigDecimal;

public record CheckResultDto(
        BigDecimal score,
        boolean passed,
        Long attemptNumber,
        String lessonTitle
) implements Dto, Serializable {}

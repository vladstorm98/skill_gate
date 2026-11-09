package com.skill_gate.dto;

import java.math.BigDecimal;

public record CheckResult(
        Long totalQuestions,
        Long correctAnswers,
        BigDecimal score,
        boolean passed
) {}

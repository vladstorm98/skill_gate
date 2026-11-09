package com.skill_gate.dto;

import java.io.Serializable;

public record AnswerOptionDto(
        Long id,
        String text,
        boolean isCorrect
) implements Dto, Serializable {
}

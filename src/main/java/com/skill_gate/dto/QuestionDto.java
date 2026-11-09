package com.skill_gate.dto;

import java.io.Serializable;
import java.util.List;

public record QuestionDto(
        Long id,
        String text,
        Long number,
        String explanation,
        List<AnswerOptionDto> options
) implements Dto, Serializable {}

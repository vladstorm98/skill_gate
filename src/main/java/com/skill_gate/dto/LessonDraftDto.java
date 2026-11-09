package com.skill_gate.dto;

import java.util.HashSet;
import java.util.Set;

public record LessonDraftDto(Long id, Set<Long> positionIds) {
    public static LessonDraftDto empty() {
        return new LessonDraftDto(null, new HashSet<>());
    }
}

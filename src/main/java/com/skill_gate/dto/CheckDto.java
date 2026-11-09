package com.skill_gate.dto;

import com.skill_gate.entity.DbLesson;

import java.io.Serializable;

public record CheckDto(
        Long id,
        DbLesson lesson,
        Long version
) implements Dto, Serializable {}

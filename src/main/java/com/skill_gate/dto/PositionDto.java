package com.skill_gate.dto;

import java.io.Serializable;

public record PositionDto(
        Long id,
        String name,
        String description
) implements Dto, Serializable {}

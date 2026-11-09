package com.skill_gate.exception;

import jakarta.persistence.EntityNotFoundException;

public class AnswerOptionNotFoundException extends EntityNotFoundException {
    public AnswerOptionNotFoundException(String message) {
        super(message);
    }
}

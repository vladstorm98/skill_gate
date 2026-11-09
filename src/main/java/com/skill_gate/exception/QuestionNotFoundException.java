package com.skill_gate.exception;

import jakarta.persistence.EntityNotFoundException;

public class QuestionNotFoundException extends EntityNotFoundException {
    public QuestionNotFoundException(String message) {
        super(message);
    }
}

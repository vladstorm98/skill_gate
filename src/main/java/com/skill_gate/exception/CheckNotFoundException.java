package com.skill_gate.exception;

import jakarta.persistence.EntityNotFoundException;

public class CheckNotFoundException extends EntityNotFoundException {
    public CheckNotFoundException(String message) {
        super(message);
    }
}

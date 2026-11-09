package com.skill_gate.exception;

public class UserBlockingException extends RuntimeException {
    public UserBlockingException(String message) {
        super(message);
    }
}

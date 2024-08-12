package com.project.exceptions;

public class ForbiddenTagException extends RuntimeException {
    public ForbiddenTagException(String message) {
        super(message);
    }
}

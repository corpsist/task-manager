package com.example.taskmanager.exception;

public class BadCredentialsException extends RuntimeException {

    public BadCredentialsException(String message) {
        super(message);
    }
}

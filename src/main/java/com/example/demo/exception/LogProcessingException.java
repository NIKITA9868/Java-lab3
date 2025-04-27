package com.example.demo.exception;

public class LogProcessingException extends RuntimeException {
    public LogProcessingException(String message) {
        super(message);
    }
}
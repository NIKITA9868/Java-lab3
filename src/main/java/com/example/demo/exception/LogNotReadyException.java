package com.example.demo.exception;

public class LogNotReadyException extends RuntimeException {
    public LogNotReadyException(String message) {
        super(message);
    }
}
package com.example.demo.exception;

import java.util.Date;
import java.util.Map;

public record ErrorDetails(
        Date timestamp,
        String message,
        String details,
        Map<String, String> errors // Добавляем поле для ошибок валидации
) {
    public ErrorDetails(Date timestamp, String message, String details) {
        this(timestamp, message, details, null);
    }
}
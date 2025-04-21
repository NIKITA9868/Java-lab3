package com.example.demo.controller;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;





@RestController
public class LogController {

    private static final String LOG_DIR = "logs";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @GetMapping(value = "/api/logs/download", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<Resource> downloadLogFile(@RequestParam String date) {
        // Проверяем корректность даты
        LocalDate parsedDate;
        try {
            parsedDate = LocalDate.parse(date, DATE_FORMAT);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }

        // Определяем имя файла
        String filename;
        if (parsedDate.isEqual(LocalDate.now())) {
            // Сегодняшние логи — берем активный файл
            filename = "application.log";
        } else {
            // Логи за прошлые дни — ищем архивный файл
            filename = "application-" + date + ".0.log";
        }

        // Полный путь к файлу
        Path filePath = Paths.get(LOG_DIR, filename);
        File logFile = filePath.toFile();

        if (!logFile.exists()) {
            return ResponseEntity.notFound().build();
        }

        // Отдаём файл как вложение
        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + filename + "\""
                )
                .contentType(MediaType.TEXT_PLAIN)
                .body(new FileSystemResource(logFile));
    }
}
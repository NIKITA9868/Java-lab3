package com.example.demo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;



@RestController
@RequestMapping("/logs")
@Tag(name = "Log Management", description = "API для работы с лог-файлами")
public class LogController {

    private static final String LOG_FILE_PATH = "application.log";
    private static final DateTimeFormatter LOG_DATE_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Operation(
            summary = "Получить логи по дате",
            description = "Возвращает список лог-записей за указанную дату."
                    + " Логи должны начинаться с даты в формате YYYY-MM-DD.",
            parameters = {
                @Parameter(
                            name = "date",
                            description = "Дата в формате YYYY-MM-DD",
                            example = "2023-05-15",
                            required = true
                    )
            }
    )
    @ApiResponses({
        @ApiResponse(
                    responseCode = "200",
                    description = "Успешный запрос",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(
                                    value = "[\"2023-05-15 10:00:00 INFO Application started\","
                                            + " \"2023-05-15 10:05:00 DEBUG Processing request\"]")
                    )
            ),
        @ApiResponse(
                    responseCode = "400",
                    description = "Неверный формат даты",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            examples = @ExampleObject(value = "Invalid date format. Use YYYY-MM-DD")
                    )
            ),
        @ApiResponse(
                    responseCode = "500",
                    description = "Ошибка сервера",
                    content = @Content(
                            mediaType = MediaType.TEXT_PLAIN_VALUE,
                            examples = @ExampleObject(value = "Error reading log file")
                    )
            )
    })
    @GetMapping(value = "/date", produces =
        {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_PLAIN_VALUE})
    public ResponseEntity<?> getLogsByDate(
            @RequestParam String date
    ) {
        try {
            LocalDate targetDate = LocalDate.parse(date, LOG_DATE_FORMAT);
            List<String> logs = filterLogsByDate(targetDate);
            return ResponseEntity.ok(logs);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Invalid date format. Use YYYY-MM-DD");
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("Error reading log file");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Internal server error");
        }
    }

    private List<String> filterLogsByDate(LocalDate targetDate) throws IOException {
        try (Stream<String> lines = Files.lines(Paths.get(LOG_FILE_PATH))) {
            return lines
                    .filter(line -> {
                        try {
                            String logDateStr = line.substring(0, 10);
                            LocalDate logDate = LocalDate.parse(logDateStr, LOG_DATE_FORMAT);
                            return logDate.equals(targetDate);
                        } catch (Exception e) {
                            return false;
                        }
                    })
                    .collect(Collectors.toList());
        }
    }
}
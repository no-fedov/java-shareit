package ru.practicum.shareit.exception.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.model.NotFoundEntityException;
import ru.practicum.shareit.user.validation.EmailIsBusy;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice("ru.practicum.shareit")
@Slf4j
public class ExceptionController {
    @ExceptionHandler({NotFoundEntityException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundEntityException(RuntimeException exception) {
        return Map.of("Request processing error", exception.getMessage());
    }

    @ExceptionHandler({EmailIsBusy.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleEmailIsBusyException(RuntimeException exception) {
        return Map.of("Request processing error", exception.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, List<String>> handleNoValidRequestBodyException(final MethodArgumentNotValidException e) {
        List<String> descriptionViolations = e.getFieldErrors().stream()
                .map(x -> {
                    return x.getField() + " -> " + x.getDefaultMessage();
                })
                .collect(Collectors.toList());
        log.warn("Тело запроса содержит невалидные данные: {}.", descriptionViolations);
        return Map.of("Тело запроса содержит некорректные данные", descriptionViolations);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, StackTraceElement[]> handleThrowableException(final Throwable e) {
        log.warn("Request processing error {}", e.getStackTrace());
        return Map.of("Request processing error", e.getStackTrace());
    }
}

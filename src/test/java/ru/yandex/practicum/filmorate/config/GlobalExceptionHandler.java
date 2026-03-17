package ru.yandex.practicum.filmorate.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.ValidationException;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ValidationException.class)
    public String handleValidationException(ValidationException ex, WebRequest request) {
        return ex.getMessage();
    }

    @ExceptionHandler(NullPointerException.class)
    public String handleValidationException(NullPointerException ex, WebRequest request) {
        return "Пустое тело запроса";
    }

    @ExceptionHandler(ConditionsNotMetException.class)
    public String handleValidationException(ConditionsNotMetException ex, WebRequest request) {
        return ex.getMessage();
    }


}

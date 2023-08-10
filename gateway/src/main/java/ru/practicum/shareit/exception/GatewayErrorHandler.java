package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ValidationException;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GatewayErrorHandler {
    @ExceptionHandler({EntityNotFoundException.class,
            AccessDeniedException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> notFound(final RuntimeException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler(AlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> conflict(final RuntimeException e) {
        return Map.of("error", e.getMessage());
    }

    @ExceptionHandler({NotAvailableBookingException.class,
            ValidationException.class,
            MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> badRequest(final RuntimeException e) {
        return Map.of("error", e.getMessage());
    }
}

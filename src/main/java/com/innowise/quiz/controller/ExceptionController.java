package com.innowise.quiz.controller;

import com.innowise.quiz.service.exception.ServiceException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ValidationException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static java.util.Objects.nonNull;

@ControllerAdvice
@RequiredArgsConstructor
public class ExceptionController {
    private static final String BASE_NAME = "error.";
    private final MessageSource messageSource;

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<Map<String, String>> handleSecurityException(SecurityException exception, Locale locale) {
        HttpStatus status = HttpStatus.FORBIDDEN;
        return handleExceptionInternal(BASE_NAME + status.name().toLowerCase(),
                status,
                locale,
                exception.getMessage());
    }

    @ExceptionHandler(ServiceException.class)
    public ResponseEntity<Map<String, String>> handleServiceException(ServiceException exception, Locale locale) {
        return handleExceptionInternal(BASE_NAME + exception.getStatus().name().toLowerCase(),
                exception.getStatus(),
                locale,
                exception.getMessage());
    }

    @ExceptionHandler({
            ValidationException.class,
            HttpMessageNotReadableException.class,
            MethodArgumentNotValidException.class,
            MethodArgumentTypeMismatchException.class
    })
    public ResponseEntity<Map<String, String>> handleServiceException(Exception exception, Locale locale) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        return handleExceptionInternal(BASE_NAME + status.name().toLowerCase(),
                status,
                locale,
                exception.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleException(Exception e, Locale locale) {
        return handleExceptionInternal("error.unexpected", HttpStatus.INTERNAL_SERVER_ERROR, locale, e.getMessage());
    }

    private ResponseEntity<Map<String, String>> handleExceptionInternal(String messageKey, HttpStatus status, Locale locale, String detailMessage) {
        Map<String, String> errorResponse = new HashMap<>();
        String message = messageSource.getMessage(messageKey, null, locale);
        errorResponse.put("errorMessage", message);
        errorResponse.put("errorCode", Integer.toString(status.value()));
        if (nonNull(detailMessage)) {
            errorResponse.put("detail", detailMessage);
        }
        return new ResponseEntity<>(errorResponse, status);
    }
}

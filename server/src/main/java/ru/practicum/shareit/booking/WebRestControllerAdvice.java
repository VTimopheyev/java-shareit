package ru.practicum.shareit.booking;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class WebRestControllerAdvice {
    @ExceptionHandler(UnsupportedBookingStatusError.class)
    public ResponseEntity<ErrorResponse> handleStatusException(
            UnsupportedBookingStatusError ex) {
        String error = "Unknown state: " + ex.getMessage();
        return ResponseEntity.status(400).body(new ErrorResponse(error));
    }
}

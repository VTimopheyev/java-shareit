package ru.practicum.shareit.booking;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Unknown state: UNSUPPORTED_STATUS")
public class BookingWrongStatusException extends RuntimeException{
    public BookingWrongStatusException() {
        super("Unknown state: UNSUPPORTED_STATUS");
    }
}

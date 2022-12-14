package ru.practicum.shareit.booking;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BookingValidationException extends RuntimeException {
    public BookingValidationException() {
        super("Booking end or start time is invalid");
    }

}

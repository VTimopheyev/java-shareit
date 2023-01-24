package ru.practicum.shareit.booking;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BookingAlreadyApprovedException extends RuntimeException {
    public BookingAlreadyApprovedException() {
        super("Booking has already been approved");
    }
}

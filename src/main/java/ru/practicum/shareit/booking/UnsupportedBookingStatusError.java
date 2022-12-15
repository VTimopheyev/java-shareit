package ru.practicum.shareit.booking;

public class UnsupportedBookingStatusError extends RuntimeException {

    public UnsupportedBookingStatusError(String status) {
        super(status);
    }
}

package ru.practicum.shareit.booking;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public class ItemNotAvailableException extends RuntimeException {

        public ItemNotAvailableException() {
            super("The item is not available for booking");
        }
    }
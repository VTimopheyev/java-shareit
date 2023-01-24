package ru.practicum.shareit.request;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ItemRequestValidationException extends RuntimeException {
    public ItemRequestValidationException() {

        super("Request description cannot be empty");
    }
}

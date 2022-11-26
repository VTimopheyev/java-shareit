package ru.practicum.shareit.item;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ItemValidationException extends RuntimeException {

    public ItemValidationException(final String message) {
        super(message);
    }

}

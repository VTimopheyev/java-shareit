package ru.practicum.shareit.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


@ResponseStatus(HttpStatus.CONFLICT)
public class UserEmailInvalidException extends RuntimeException {
    public UserEmailInvalidException(final String message) {
        super(message);
    }
}



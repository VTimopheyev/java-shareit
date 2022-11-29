package ru.practicum.shareit.user;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserValidationException extends RuntimeException {

    public UserValidationException() {

        super("Wrong User credentials: invalid/empty email or empty name are tried to be set");
    }

}

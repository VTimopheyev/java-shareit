package ru.practicum.shareit.request;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class PagingValidationException extends RuntimeException{

    public PagingValidationException() {
        super("Paging parameters are wrong");
    }
}

package ru.practicum.shareit.item;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CommentValidationException extends RuntimeException{
    public CommentValidationException() {
        super("Comment is invalid and cannot be created");
    }
}

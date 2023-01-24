package ru.practicum.shareit.booking;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class CommentsCreationIsNotAvailable extends RuntimeException {
    public CommentsCreationIsNotAvailable() {
        super("User cannot add comment to items he`s never booked yet");
    }
}

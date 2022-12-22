package ru.practicum.shareit.item;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ItemRequestNotFoundException extends RuntimeException {
    public ItemRequestNotFoundException() {

        super("Request for the item not found");
    }
}

package ru.practicum.shareit.booking;

import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
public class ErrorResponse {

    private String error;

    public ErrorResponse (String error){
        this.error = error;
    }

}

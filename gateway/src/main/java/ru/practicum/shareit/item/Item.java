package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    private long id;
    private String name;
    private String description;
    private boolean available;
    private User owner;
    private ItemRequest request;

}

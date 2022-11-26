package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.User;

import java.util.List;

public interface ItemStorage {


    List<ItemDto> getItems(Long aLong);

    ItemDto addNewItem(ItemDto itemDTO, long userId);

    ItemDto getItem(long itemId);

    Long checkOwner(long itemId);

    ItemDto updateItem(long userId, long itemId, ItemDto itemDTO);

    void deleteItem(long itemId);

    User getOwner(long itemId);

    List<ItemDto> searchItems(String text);
}

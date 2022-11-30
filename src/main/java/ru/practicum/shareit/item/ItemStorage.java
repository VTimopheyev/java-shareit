package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {


    List<Item> getItems(Long aLong);

    Long addNewItem(Item item);

    Item getItem(long itemId);

    Item updateItem(long userId, long itemId, ItemDto itemDTO);

    void deleteItem(long itemId);

    List<Item> searchItems(String text);
}

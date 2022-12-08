package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemService {

    List<ItemDto> getItems(Optional<Long> userId);

    ItemDto getItem(Optional<Long> userId, long itemId);

    ItemDto addNewItem(Optional<Long> userId, ItemDto itemDTO);

    ItemDto updateItem(Optional<Long> userId, long itemId, ItemDto itemDTO);

    void deleteItem(Optional<Long> userId, long itemId);

    List<ItemDto> searchItems(Optional<Long> userId, String text);

    Item getItemById(Long itemId);
}

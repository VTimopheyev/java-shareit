package ru.practicum.shareit.item;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OwnerItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface ItemService {

    ItemDto addNewItem(Optional<Long> userId, ItemDto itemDTO);

    OwnerItemDto convertToOwnerItemDto(Item i, BookingDto lastBooking, BookingDto nextBooking);

    List<Item> getAllItems(User user, Integer from, Integer size);

    ItemDto updateItem(Optional<Long> userId, long itemId, ItemDto itemDTO);

    void deleteItem(Optional<Long> userId, long itemId);

    List<ItemDto> searchItems(Optional<Long> userId, String text, Integer from, Integer size);

    Optional<Item> getItemById(Long itemId);

    boolean checkItemExists(Long id);

    OwnerItemDto getItemForOwner(Item item, BookingDto lastBooking, BookingDto nextBooking);

    Comment addNewComment(Optional<Long> userId, long itemId, Comment comment);
}

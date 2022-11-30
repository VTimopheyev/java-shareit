package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserNotFoundException;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemService {

    private final ItemStorage itemStorage;
    private final UserService userService;
    private final ItemMapper itemMapper;


    public ItemDto addNewItem(Optional<Long> userId, ItemDto itemDTO) {
        if (userId.isEmpty()) {
            throw new UserValidationException();
        }

        if (!userService.checkUserExists(userId.get())) {
            throw new UserNotFoundException();
        }

        if (validateItemDTO(itemDTO)) {
            Item item = itemMapper.toItem(itemDTO);
            Long id = itemStorage.addNewItem(item);
            itemStorage.getItem(id).setOwner(userService.getOwner(userId.get()));

            return itemMapper.toItemDto(itemStorage.getItem(id));
        }
        return null;
    }

    public ItemDto getItem(Optional<Long> userId, long itemId) {
        if (userId.isEmpty() || !userService.checkUserExists(userId.get()) ||
                Optional.of(itemStorage.getItem(itemId)).isEmpty()) {
            throw new UserValidationException();
        }
        return itemMapper.toItemDto(itemStorage.getItem(itemId));
    }

    public List<ItemDto> getItems(Optional<Long> userId) {

        if (userId.isEmpty() || !userService.checkUserExists(userId.get())) {
            throw new UserValidationException();
        }

        List<Item> allItems = itemStorage.getItems(userId.get());
        List<ItemDto> allDtoItems = new ArrayList<>();

        for (Item i : allItems) {
            allDtoItems.add(itemMapper.toItemDto(i));
        }


        return allDtoItems;
    }

    public ItemDto updateItem(Optional<Long> userId, long itemId, ItemDto itemDTO) {
        if (userId.isEmpty() || !userService.checkUserExists(userId.get()) ||
                Optional.of(itemStorage.getItem(itemId)).isEmpty()) {
            throw new UserValidationException();
        }

        if (!checkItemOwnedByUser(userId, itemId)) {
            throw new ItemNotFoundException();
        }

        return itemMapper.toItemDto(itemStorage.updateItem(userId.get(), itemId, itemDTO));
    }

    public void deleteItem(Optional<Long> userId, long itemId) {
        if (userId.isEmpty() || !userService.checkUserExists(userId.get()) ||
                Optional.of(itemStorage.getItem(itemId)).isEmpty()) {
            throw new UserValidationException();
        }

        if (!checkItemOwnedByUser(userId, itemId)) {
            throw new ItemNotFoundException();
        }

        itemStorage.deleteItem(itemId);
    }


    public List<ItemDto> searchItems(Optional<Long> userId, String text) {
        if (userId.isEmpty() || !userService.checkUserExists(userId.get())) {
            throw new UserValidationException();
        }

        if (text.isEmpty()) {
            return new ArrayList<>();
        }

        List<Item> searchedItems = itemStorage.searchItems(text);
        List<ItemDto> searchedItemsDto = new ArrayList<>();
        for (Item i : searchedItems) {
            searchedItemsDto.add(itemMapper.toItemDto(i));
        }

        return searchedItemsDto;
    }

    private boolean validateItemDTO(ItemDto itemDTO) {
        if (itemDTO.getName() == null || itemDTO.getName().isEmpty()) {
            throw new ItemValidationException();
        }
        if (itemDTO.getDescription() == null || itemDTO.getDescription().isEmpty()) {
            throw new ItemValidationException();
        }
        if (itemDTO.getAvailable() == null) {
            throw new ItemValidationException();
        }
        return true;
    }

    private boolean checkItemOwnedByUser(@NotNull Optional<Long> userId, Long itemId) {
        return userId.get() == (itemStorage.getItem(itemId).getOwner().getId());
    }

}



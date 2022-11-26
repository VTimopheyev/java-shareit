package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserNotFoundException;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.UserValidationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemService {

    private final ItemStorage itemStorage;
    private final UserStorage userStorage;


    public ItemDto addNewItem(Optional<Long> userId, ItemDto itemDTO) {
        if (userId.isEmpty()) {
            throw new UserValidationException("User not signed in");
        }

        if (!userStorage.checkUserExists(userId.get())) {
            throw new UserNotFoundException("No such user");
        }

        if (validateItemDTO(itemDTO)) {
            return itemStorage.addNewItem(itemDTO, userId.get());
        }
        return null;
    }

    public ItemDto getItem(Optional<Long> userId, long itemId) {
        if (userId.isEmpty() || !userStorage.checkUserExists(userId.get()) ||
                Optional.of(itemStorage.getItem(itemId)).isEmpty()) {
            throw new UserValidationException("No such user or user not signed in");
        }
        return itemStorage.getItem(itemId);
    }

    public List<ItemDto> getItems(Optional<Long> userId) {

        if (userId.isEmpty() || !userStorage.checkUserExists(userId.get())) {
            throw new UserValidationException("No such user or user not signed in");
        }

        return itemStorage.getItems(userId.get());
    }

    public ItemDto updateItem(Optional<Long> userId, long itemId, ItemDto itemDTO) {
        if (userId.isEmpty() || !userStorage.checkUserExists(userId.get()) ||
                Optional.of(itemStorage.getItem(itemId)).isEmpty()) {
            throw new UserValidationException("No such user or user not signed in");
        }

        if (!checkItemOwnedByUser(userId, itemId)) {
            throw new ItemNotFoundException("The item is owned by another user");
        }

        return itemStorage.updateItem(userId.get(), itemId, itemDTO);
    }

    public void deleteItem(Optional<Long> userId, long itemId) {
        if (userId.isEmpty() || !userStorage.checkUserExists(userId.get()) ||
                Optional.of(itemStorage.getItem(itemId)).isEmpty()) {
            throw new UserValidationException("No such user or user not signed in");
        }

        if (!checkItemOwnedByUser(userId, itemId)) {
            throw new ItemNotFoundException("The item is owned by another user");
        }

        itemStorage.deleteItem(itemId);
    }


    public List<ItemDto> searchItems(Optional<Long> userId, String text) {
        if (userId.isEmpty() || !userStorage.checkUserExists(userId.get())) {
            throw new UserValidationException("No such user or user not signed in");
        }

        if (text.isEmpty()) {
            return new ArrayList<>();
        }

        return itemStorage.searchItems(text);
    }

    private boolean validateItemDTO(ItemDto itemDTO) {
        if (itemDTO.getName() == null || itemDTO.getName().isEmpty()) {
            throw new ItemValidationException("Wrong item name");
        }
        if (itemDTO.getDescription() == null || itemDTO.getDescription().isEmpty()) {
            throw new ItemValidationException("Wrong item description");
        }
        if (itemDTO.getAvailable() == null) {
            //System.out.println(Optional.of(itemDTO.isAvailable()).isEmpty());
            throw new ItemValidationException("Wrong item availability");
        }
        return true;
    }

    private boolean checkItemOwnedByUser(Optional<Long> userId, long itemId) {
        return userId.get() == itemStorage.checkOwner(itemId);
    }

}



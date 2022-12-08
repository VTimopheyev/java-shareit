package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class ItemServiceImpl implements ItemService {
    private final UserService userService;
    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;


    public ItemDto addNewItem(Optional<Long> userId, ItemDto itemDTO) {
        if (userId.isEmpty()) {
            throw new UserValidationException();
        }

        if (!userService.checkUserExists(userId.get())) {
            throw new UserNotFoundException();
        }

        if (validateItemDTO(itemDTO)) {
            Item item = itemMapper.toItem(itemDTO);
            item.setOwner(userService.getOwner(userId.get()));

            return itemMapper.toItemDto(itemRepository.save(item));
        }
        return null;
    }

    public ItemDto getItem(Optional<Long> userId, long itemId) {
        if (userId.isEmpty() || !userService.checkUserExists(userId.get())) {
            throw new UserValidationException();
        }

        if (!itemRepository.existsById(itemId)) {
            throw new ItemNotFoundException();
        }

        return itemMapper.toItemDto(itemRepository.getById(itemId));
    }

    public List<ItemDto> getItems(Optional<Long> userId) {

        if (userId.isEmpty() || !userService.checkUserExists(userId.get())) {
            throw new UserValidationException();
        }

        List<Item> allItems = itemRepository.findByOwnerEquals(userService.getOwner(userId.get()));
        List<ItemDto> allDtoItems = new ArrayList<>();

        for (Item i : allItems) {
            allDtoItems.add(itemMapper.toItemDto(i));
        }

        return allDtoItems;
    }

    public ItemDto updateItem(Optional<Long> userId, long itemId, ItemDto itemDTO) {
        if (userId.isEmpty() || !userService.checkUserExists(userId.get()) ||
                Optional.of(itemRepository.getById(itemId)).isEmpty()) {
            throw new UserValidationException();
        }

        if (!checkItemOwnedByUser(userId, itemId)) {
            throw new ItemNotFoundException();
        }

        Item item = itemRepository.getById(itemId);
        if (itemDTO.getName() != null) {
            item.setName(itemDTO.getName());
        }
        if (itemDTO.getAvailable() != null) {
            item.setAvailable(itemDTO.getAvailable());
        }
        if (itemDTO.getDescription() != null) {
            item.setDescription(itemDTO.getDescription());
        }

        return itemMapper.toItemDto(itemRepository.save(item));
    }

    public void deleteItem(Optional<Long> userId, long itemId) {
        if (userId.isEmpty() || !userService.checkUserExists(userId.get()) ||
                Optional.of(itemRepository.getById(itemId)).isEmpty()) {
            throw new UserValidationException();
        }

        if (!checkItemOwnedByUser(userId, itemId)) {
            throw new ItemNotFoundException();
        }
        itemRepository.deleteById(itemId);
    }


    public List<ItemDto> searchItems(Optional<Long> userId, String text) {
        if (userId.isEmpty() || !userService.checkUserExists(userId.get())) {
            throw new UserValidationException();
        }

        if (text.isEmpty()) {
            return new ArrayList<>();
        }

        List<Item> searchedItems =
                itemRepository.findByNameContainingOrDescriptionContainingIgnoreCaseAndAvailableEquals
                        (text, text, true);
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

    private boolean checkItemOwnedByUser(Optional<Long> userId, Long itemId) {
        return userId.get() == (itemRepository.getReferenceById(itemId).getOwner().getId());
    }
}



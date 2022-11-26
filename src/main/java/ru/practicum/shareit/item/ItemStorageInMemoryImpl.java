package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserStorage;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class ItemStorageInMemoryImpl implements ItemStorage {

    private long idCount = 1;
    private Map<Long, Item> items = new HashMap<>();
    private final ItemMapper itemMapper;
    private final UserStorage userStorage;

    @Override
    public ItemDto addNewItem(ItemDto itemDTO, long userId) {
        Item item = itemMapper.toItem(itemDTO);
        item.setId(idCount);
        idCount++;
        item.setOwner(userStorage.getUser(userId));
        items.put(item.getId(), item);

        return getItem(item.getId());
    }

    @Override
    public ItemDto getItem(long itemId) {
        return itemMapper.toItemDto(items.get(itemId));
    }

    @Override
    public Long checkOwner(long itemId) {
        return items.get(itemId).getOwner().getId();
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDTO) {
        Item updatedItem = items.get(itemId);

        if (itemDTO.getAvailable() != null) {
            updatedItem.setAvailable(itemDTO.getAvailable());
        }

        if (itemDTO.getDescription() != null) {
            updatedItem.setDescription(itemDTO.getDescription());
        }
        if (itemDTO.getName() != null) {
            updatedItem.setName(itemDTO.getName());
        }

        items.put(itemId, updatedItem);

        return itemMapper.toItemDto(items.get(itemId));
    }

    @Override
    public void deleteItem(long itemId) {
        items.remove(itemId);
    }


    @Override
    public List<ItemDto> getItems(Long userId) {
        List<ItemDto> list = new ArrayList<>();
        for (long id : items.keySet()) {
            if (items.get(id).getOwner().getId() == userId) {
                list.add(itemMapper.toItemDto(items.get(id)));
            }
        }
        return list;
    }

    @Override
    public User getOwner(long itemId) {
        return items.get(itemId).getOwner();
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        List<ItemDto> searchedItems = new ArrayList<>();
        for (Item item : items.values()) {
            if ((StringUtils.containsIgnoreCase(item.getName(), text) ||
                    StringUtils.containsIgnoreCase(item.getDescription(), text)) &&
                    item.isAvailable()) {
                searchedItems.add(itemMapper.toItemDto(item));
            }
        }
        return searchedItems;
    }
}

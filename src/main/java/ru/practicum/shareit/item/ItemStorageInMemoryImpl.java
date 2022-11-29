package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@Component
public class ItemStorageInMemoryImpl implements ItemStorage {

    private long idCount = 1;
    private Map<Long, Item> items = new HashMap<>();


    @Override
    public Long addNewItem(Item item) {
        item.setId(idCount);
        idCount++;
        items.put(item.getId(), item);

        return getItem(item.getId()).getId();
    }

    @Override
    public Item getItem(long itemId) {
        return items.get(itemId);
    }

    @Override
    public Item updateItem(long userId, long itemId, ItemDto itemDTO) {
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

        return updatedItem;
    }

    @Override
    public void deleteItem(long itemId) {
        items.remove(itemId);
    }


    @Override
    public List<Item> getItems(Long userId) {
        List<Item> list = new ArrayList<>();
        for (long id : items.keySet()) {
            if (items.get(id).getOwner().getId() == userId) {
                list.add(items.get(id));
            }
        }
        return list;
    }

    @Override
    public List<Item> searchItems(String text) {
        List<Item> searchedItems = new ArrayList<>();
        for (Item item : items.values()) {
            if ((StringUtils.containsIgnoreCase(item.getName(), text) ||
                    StringUtils.containsIgnoreCase(item.getDescription(), text)) &&
                    item.isAvailable()) {
                searchedItems.add(item);
            }
        }
        return searchedItems;
    }
}

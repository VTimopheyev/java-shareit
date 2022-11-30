package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Component
@RequiredArgsConstructor
@Slf4j
public class ItemMapper {

    public ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable()
        );
    }

    public Item toItem(ItemDto itemDTO) {
        return new Item(
                itemDTO.getName(),
                itemDTO.getDescription(),
                itemDTO.getAvailable()
        );
    }

}

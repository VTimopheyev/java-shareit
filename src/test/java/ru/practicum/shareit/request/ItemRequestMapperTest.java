package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestMapperTest {

    @InjectMocks
    private ItemRequestMapper itemRequestMapper;

    @Test
    void toItemRequestDto() {
        List<ItemDto> items = new ArrayList<>();
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Some");
        itemRequest.setCreated(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));

        ItemRequestDto itemRequestDto = itemRequestMapper.toItemRequestDto(itemRequest, items);

        assertNotNull(itemRequestDto);
    }
}
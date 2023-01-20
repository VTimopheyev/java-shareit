package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OwnerItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ItemMapperTest {

    @InjectMocks
    private ItemMapper itemMapper;

    @Test
    void toItemDtoTest() {
        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);

        List<Comment> comment = new ArrayList<>();

        Item item = new Item();
        item.setId(2L);
        item.setName("Bob");
        item.setDescription("Some description");
        item.setAvailable(true);
        item.setRequest(itemRequest);

        ItemDto itemDto = itemMapper.toItemDto(item, comment);

        assertNotNull(itemDto);
    }

    @Test
    void toItemTest() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Bob");
        itemDto.setDescription("Some");
        itemDto.setAvailable(true);

        Item item = itemMapper.toItem(itemDto);

        assertNotNull(item);
    }

    @Test
    void toOwnerItemDtoTest() {
        Item item = new Item();
        item.setId(2L);
        item.setName("Bob");
        item.setDescription("Some description");
        item.setAvailable(true);

        BookingDto booking = new BookingDto();
        BookingDto booking1 = new BookingDto();

        List<Comment> comments = new ArrayList<>();

        OwnerItemDto ownerItemDto = itemMapper.toOwnerItemDto(item, booking, booking1, comments);

        assertNotNull(ownerItemDto);

    }

    @Test
    void testToItem() {
        OwnerItemDto item = new OwnerItemDto();
        item.setName("Bob");
        item.setDescription("Some description");
        item.setAvailable(true);

        Item item1 = itemMapper.toItem(item);

        assertNotNull(item1);
    }
}
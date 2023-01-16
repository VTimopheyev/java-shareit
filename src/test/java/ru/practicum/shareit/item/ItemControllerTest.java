package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.OwnerItemDto;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ItemService itemService;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private ItemRequestService itemRequestService;

    @MockBean
    private UserService userService;

    @SneakyThrows
    @Test
    void addItemTest() {
        Optional<Long> userId = Optional.of(2L);
        ItemDto itemDto = new ItemDto();

        when(itemService.addNewItem(userId, itemDto)).thenReturn(itemDto);

        String result = mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 2)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDto), result);
    }

    @SneakyThrows
    @Test
    void update() {
        Optional<Long> userId = Optional.of(2L);
        ItemDto itemDto = new ItemDto();
        long itemId = 1L;

        when(itemService.updateItem(userId, itemId, itemDto)).thenReturn(itemDto);

        String result = mockMvc.perform(patch("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", 2)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemDto), result);
    }

    @SneakyThrows
    @Test
    void deleteItem() {
        Optional<Long> userId = Optional.of(2L);
        ItemDto itemDto = new ItemDto();
        long itemId = 1L;

        mockMvc.perform(delete("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk());

        verify(itemService).deleteItem(userId, itemId);
    }

    @SneakyThrows
    @Test
    void searchItems() {
        Optional<Long> userId = Optional.of(2L);
        ItemDto itemDto = new ItemDto();
        Integer size = 5;
        Integer from = 0;
        String text = "search text";
        List<ItemDto> list = new ArrayList<>();

        when(itemService.searchItems(userId, text, from, size)).thenReturn(list);

        String result = mockMvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 2)
                        .param("text", String.valueOf("search text"))
                        .param("from", String.valueOf("0"))
                        .param("size", String.valueOf("5")))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(list), result);
    }

    @SneakyThrows
    @Test
    void addComment() {
        Optional<Long> userId = Optional.of(2L);
        long itemId = 1L;
        Comment comment = new Comment();

        when(itemService.addNewComment(userId, itemId, comment)).thenReturn(comment);

        String result = mockMvc.perform(post("/items/{itemId}/comment", itemId)
                        .header("X-Sharer-User-Id", 2)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(comment)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(comment), result);
    }

    @SneakyThrows
    @Test
    void getItem() {
        Optional<Long> userId = Optional.of(2L);
        long itemId = 1L;

        mockMvc.perform(get("/items/{itemId}", itemId)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk());

        verify(bookingService).getItem(userId, itemId);
    }

    @SneakyThrows
    @Test
    void getItems() {
        Optional<Long> userId = Optional.of(2L);
        Integer size = 5;
        Integer from = 0;
        List<OwnerItemDto> list = new ArrayList<>();

        when(bookingService.getAllItems(userId, from, size)).thenReturn(list);

        String result = mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 2)
                        .param("from", String.valueOf("0"))
                        .param("size", String.valueOf("5")))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(list), result);
    }
}
package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class ItemRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ItemRequestService itemRequestService;
    @MockBean
    private BookingController bookingController;
    @MockBean
    private BookingService bookingService;
    @MockBean
    private ItemService itemService;
    @MockBean
    private UserController userController;
    @MockBean
    private UserService userService;

    @SneakyThrows
    @Test
    void add() {
        Optional<Long> userId = Optional.of(2L);
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        ItemRequest itemRequest = new ItemRequest();

        when(itemRequestService.addNewRequest(userId, itemRequestDto)).thenReturn(itemRequest);

        String result = mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 2)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(itemRequest), result);
    }

    @SneakyThrows
    @Test
    void getUserRequests() {
        Optional<Long> userId = Optional.of(2L);

        when(itemRequestService.getUserRequests(userId)).thenReturn(new ArrayList<>());

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk());

        verify(itemRequestService).getUserRequests(userId);
    }

    @SneakyThrows
    @Test
    void getAllRequests() {
        Optional<Long> userId = Optional.of(2L);
        Integer size = 5;
        Integer from = 0;
        List<ItemRequestDto> list = new ArrayList<>();

        when(itemRequestService.getAllRequests(from, size, userId)).thenReturn(list);

        String result = mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 2)
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
    void testGetRequest() {
        Optional<Long> userId = Optional.of(2L);
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        long requestId = 1L;

        when(itemRequestService.getRequest(userId, requestId)).thenReturn(itemRequestDto);

        mockMvc.perform(get("/requests/{requestId}", requestId)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk());

        verify(itemRequestService).getRequest(userId, requestId);
    }
}
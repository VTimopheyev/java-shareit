package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;
    @MockBean
    private BookingController bookingController;
    @MockBean
    private ItemController itemController;
    @MockBean
    private ItemRequestController itemRequestController;


    @SneakyThrows
    @Test
    void findAll() {
        List<UserDto> list = new ArrayList<>();

        when(userService.getAll()).thenReturn(list);

        String result = mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(list), result);
    }

    @SneakyThrows
    @Test
    void getUser() {
        long id = 1L;

        mockMvc.perform(get("/users/{userId}", id)).andExpect(status().isOk());

        verify(userService).getUser(id);
    }

    @SneakyThrows
    @Test
    void create() {
        UserDto userDto = new UserDto();

        when(userService.create(userDto)).thenReturn(userDto);

        String result = mockMvc.perform(post("/users")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDto), result);

    }

    @SneakyThrows
    @Test
    void update() {
        UserDto userDto = new UserDto();
        long id = 1L;

        when(userService.update(id, userDto)).thenReturn(userDto);

        String result = mockMvc.perform(patch("/users/{id}", id)
                        .header("X-Sharer-User-Id", 2)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(userDto), result);

    }

    @Test
    @SneakyThrows
    void deleteUser() {
        long userId = 1L;

        mockMvc.perform(delete("/users/{userId}", userId))
                .andExpect(status().isOk());

        verify(userService).deleteUser(userId);
    }
}
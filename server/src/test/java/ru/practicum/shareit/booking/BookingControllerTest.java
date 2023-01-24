package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.request.ItemRequestController;
import ru.practicum.shareit.user.UserController;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class BookingControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private ItemController itemController;

    @MockBean
    private ItemRequestController itemRequestController;

    @MockBean
    private UserController userController;

    @SneakyThrows
    @Test
    public void getBookingTest() {
        Optional<Long> userId = Optional.of(2L);
        long bookingId = 1L;

        mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", 2))
                .andExpect(status().isOk());

        verify(bookingService).getBooking(userId, bookingId);
    }

    @SneakyThrows
    @Test
    public void setApprovalStatusTest() {
        Optional<Long> userId = Optional.of(2L);
        long bookingId = 1L;
        Booking booking = new Booking();
        Boolean approved = true;

        when(bookingService.setApprovalStatus(userId, bookingId, approved)).thenReturn(booking);

        String result = mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", 2)
                        .param("approved", String.valueOf(true)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(booking), result);

    }

    @SneakyThrows
    @Test
    public void addBookingTest() {
        Booking booking = new Booking();
        Optional<Long> userId = Optional.of(2L);
        BookingDto bookingDto = new BookingDto();

        when(bookingService.addNewBooking(userId, bookingDto)).thenReturn(booking);

        String result = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(booking)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(booking), result);
    }

    @SneakyThrows
    @Test
    public void getBookingsOfUserTest() {
        Optional<Long> userId = Optional.of(2L);
        List<Booking> bookings = new ArrayList<>();
        String state = "ALL";
        int from = 5;
        int size = 10;

        when(bookingService.getBookingsOfUser(userId, state, from, size)).thenReturn(bookings);

        String result = mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 2)
                        .param("state", "ALL")
                        .param("from", "5")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookings), result);
    }

    @SneakyThrows
    @Test
    public void getBookingsOfItemsOfUserTest() {
        Optional<Long> userId = Optional.of(2L);
        List<Booking> bookings = new ArrayList<>();
        String state = "ALL";
        int from = 5;
        int size = 10;

        when(bookingService.getBookedItemsOfUser(userId, state, from, size)).thenReturn(bookings);

        String result = mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 2)
                        .param("state", "ALL")
                        .param("from", "5")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookings), result);
    }


}

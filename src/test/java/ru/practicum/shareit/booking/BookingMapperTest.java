package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingMapperTest {

    @InjectMocks
    private BookingMapper bookingMapper;

    @Mock
    private ItemService itemService;


    @Test
    void toBooking() {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setStart(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));
        bookingDto.setEnd(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));

        when(itemService.getItemById(anyLong())).thenReturn(Optional.of(new Item()));

        Booking booking = bookingMapper.toBooking(bookingDto);

        assertNotNull(booking);
    }

    @Test
    void toBookingDto() {
        Item item = new Item();
        item.setId(2L);

        User user = new User();
        user.setId(1L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));
        booking.setEnd(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));
        booking.setItem(item);
        booking.setBooker(user);

        BookingDto bookingDto = bookingMapper.toBookingDto(booking);

        assertNotNull(bookingDto);
    }
}
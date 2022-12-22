package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import static java.time.LocalTime.now;
import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MINUTES;


@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTest {

    @Autowired
    private BookingService bookingService;



    @Test
    public void addNewBookingTest() {

        BookingRepository bookingRepository = Mockito.mock(BookingRepository.class);
        /*UserService userService = Mockito.mock(UserService.class);
        ItemMapper itemMapper = Mockito.mock(ItemMapper.class);
        ItemService itemService = Mockito.mock(ItemService.class);*/

        ReflectionTestUtils.setField(bookingService, "bookingRepository", bookingRepository);
        /*ReflectionTestUtils.setField(bookingService, "userService", userService);
        ReflectionTestUtils.setField(bookingService, "itemMapper", itemMapper);
        ReflectionTestUtils.setField(bookingService, "itemService", itemService);*/

        BookingDto bookingDto1;
        bookingDto1 = new BookingDto();
        bookingDto1.setStart(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));
        bookingDto1.setEnd(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));
        bookingDto1.setItemId(1L);
        bookingService.addNewBooking(Optional.of(1L), bookingDto1);
    }


}

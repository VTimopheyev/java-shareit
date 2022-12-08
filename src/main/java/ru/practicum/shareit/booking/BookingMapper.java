package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemService;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookingMapper {
    private final ItemService itemService;

    public Booking toBooking(BookingDto bookingDto) {
        return new Booking(
                bookingDto.getStart(),
                bookingDto.getEnd(),
                itemService.getItemById(bookingDto.getItemId())
        );
    }

    public BookingDto toBookingDto(Booking booking) {
        return new BookingDto(
                booking.getItem().getId(),
                booking.getStart(),
                booking.getEnd()
        );
    }

}

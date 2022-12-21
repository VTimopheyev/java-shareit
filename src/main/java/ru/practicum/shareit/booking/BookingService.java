package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.OwnerItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface BookingService {

    Booking addNewBooking(Optional<Long> userId, BookingDto bookingDto);

    Booking setApprovalStatus(Optional<Long> userId, long bookingId, Boolean status);

    Booking getBooking(Optional<Long> userId, long bookingId);

    List<Booking> getBookingsOfUser(Optional<Long> userId, String status);

    List<Booking> getBookedItemsOfUser(Optional<Long> userId, String status);

    BookingDto getLastBooking(Item item);

    BookingDto getNextBooking(Item item);

    Object getItem(Optional<Long> userId, long itemId);

    List<OwnerItemDto> getAllItems(Optional<Long> userId);

    void checkUserBookedItem(long itemId, Optional<Long> userId);
}

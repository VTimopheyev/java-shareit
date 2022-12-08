package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.Optional;

public interface BookingService {

    Booking addNewBooking(Optional<Long> userId, BookingDto bookingDto);

    Booking setApprovalStatus(Optional<Long> userId, long bookingId, Boolean status);

    Booking getBooking(Optional<Long> userId, long bookingId);

    List<Booking> getBookingsOfUser(Optional<Long> userId, BookingStatus status);

    List<Booking> getBookedItemsOfUser(Optional<Long> userId, BookingStatus status);
}

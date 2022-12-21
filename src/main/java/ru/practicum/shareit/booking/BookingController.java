package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping(path = "/bookings")
    public Booking add(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                       @RequestBody BookingDto bookingDto) {
        return bookingService.addNewBooking(userId, bookingDto);
    }

    @PatchMapping("/bookings/{bookingId}")
    public Booking setApprovalStatus(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                     @PathVariable long bookingId,
                                     @RequestParam Boolean approved) {
        return bookingService.setApprovalStatus(userId, bookingId, approved);
    }

    @GetMapping("/bookings/{bookingId}")
    public Booking getBooking(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                              @PathVariable long bookingId) {
        return bookingService.getBooking(userId, bookingId);
    }

    @GetMapping("/bookings")
    public List<Booking> getBookingsOfUser(@RequestHeader("X-Sharer-User-Id") Optional<Long> bookerId,
                                           @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getBookingsOfUser(bookerId, state);
    }

    @GetMapping("/bookings/owner")
    public List<Booking> getBookingsOfItemsOfUser(@RequestHeader("X-Sharer-User-Id") Optional<Long> ownerId,
                                                  @RequestParam(defaultValue = "ALL") String state) {
        return bookingService.getBookedItemsOfUser(ownerId, state);
    }
}

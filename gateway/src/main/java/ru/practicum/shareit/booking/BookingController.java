package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.user.UserValidationException;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Optional;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                           @RequestBody @Valid BookItemRequestDto requestDto) {
        checkUserIdPresent(userId);
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId.get(), requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> setApprovalStatus(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                                    @PathVariable long bookingId,
                                                    @RequestParam Boolean approved) {
        checkUserIdPresent(userId);
        log.info("Changing booking {} status to {}", bookingId, approved);
        return bookingClient.setApprovalStatus(userId.get(), bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader("X-Sharer-User-Id") Optional<Long> userId,
                                             @PathVariable Long bookingId) {
        checkUserIdPresent(userId);
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId.get(), bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsOfUser(@RequestHeader("X-Sharer-User-Id") Optional<Long> bookerId,
                                                    @RequestParam(name = "state", defaultValue = "all") String state,
                                                    @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                                    @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState stateParam = BookingState.from(state)
                .orElseThrow(() -> new UnsupportedBookingStatusError(state));
        checkUserIdPresent(bookerId);
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, bookerId.get(), from, size);
        return bookingClient.getBookingsOfUser(bookerId.get(), stateParam, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsOfItemsOfUser(@RequestHeader("X-Sharer-User-Id") Optional<Long> ownerId,
                                                           @RequestParam(defaultValue = "ALL") String state,
                                                           @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                           @Positive @RequestParam(defaultValue = "5") Integer size) {
        BookingState stateParam = BookingState.from(state)
                .orElseThrow(() -> new UnsupportedBookingStatusError(state));
        checkUserIdPresent(ownerId);
        log.info("Get booking with state {}, ownerId={}, from={}, size={}", stateParam, ownerId, from, size);
        return bookingClient.getBookedItemsOfUser(ownerId.get(), stateParam, from, size);
    }

    private void checkUserIdPresent(Optional<Long> userId) {
        if (userId.isEmpty()) {
            throw new UserValidationException();
        }
    }
}

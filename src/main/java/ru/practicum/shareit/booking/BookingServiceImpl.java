package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserValidationException;

import java.util.List;
import java.util.Optional;

import static ru.practicum.shareit.booking.BookingStatus.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final ItemService itemService;
    private final BookingMapper bookingMapper;

    @Override
    public Booking addNewBooking(Optional<Long> userId, BookingDto bookingDto) {

        Booking booking = bookingMapper.toBooking(bookingDto);
        if (booking.getItem().isAvailable()) {
            booking.setBooker(userService.getOwner(userId.get()));
            booking.setStatus(BookingStatus.WAITING);
            return bookingRepository.save(booking);
        } else {
            throw new ItemNotAvailableException();
        }
    }

    @Override
    public Booking setApprovalStatus(Optional<Long> userId, long bookingId, Boolean status) {
        if (userId.isEmpty()) {
            throw new UserValidationException();
        }

        if (bookingRepository.existsById(bookingId)) {
            Booking booking = bookingRepository.getById(bookingId);
            if (status) {
                booking.setStatus(APPROVED);
                return bookingRepository.save(booking);
            } else {
                booking.setStatus(REJECTED);
                return bookingRepository.save(booking);
            }
        } else {
            throw new BookingNotFoundException();
        }
    }

    @Override
    public Booking getBooking(Optional<Long> userId, long bookingId) {
        if (userId.isEmpty()) {
            throw new UserValidationException();
        }

        if (bookingRepository.existsById(bookingId)) {
            return bookingRepository.getById(bookingId);
        } else {
            throw new BookingNotFoundException();
        }
    }

    @Override
    public List<Booking> getBookingsOfUser(Optional<Long> userId, BookingStatus status) {
        if (userId.isEmpty()) {
            throw new UserValidationException();
        }

        if (status.equals(All)){
            return bookingRepository.findByBookerEquals(userService.getOwner(userId.get()));
        }
        else {
            return bookingRepository.findByBookerEqualsAndStatusEquals(userService.getOwner(userId.get()), status);
        }
    }

    @Override
    public List<Booking> getBookedItemsOfUser(Optional<Long> userId, BookingStatus status) {
        if (userId.isEmpty()) {
            throw new UserValidationException();
        }

        if (status.equals(All)){
            return bookingRepository.findByBookerEquals(userService.getOwner(userId.get()));
        }
        else {
            return bookingRepository.findByBookerEqualsAndStatusEquals(userService.getOwner(userId.get()), status);
        }
    }
}

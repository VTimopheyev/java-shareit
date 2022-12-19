package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemNotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.OwnerItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserNotFoundException;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserValidationException;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
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

        if (validateBooking(bookingDto, userId)) {
            Booking booking = bookingMapper.toBooking(bookingDto);
            booking.setBooker(userService.getOwner(userId.get()));
            booking.setOwner(userService.getOwner(itemService.getItemById(bookingDto.getItemId()).get().getOwner().getId()));
            booking.setStatus(BookingStatus.WAITING);
            return bookingRepository.save(booking);
        }
        return null;
    }

    private boolean validateBooking(BookingDto bookingDto, Optional<Long> userId) {
        if (userId.isEmpty() || !userService.checkUserExists(userId.get())) {
            throw new UserNotFoundException();
        }

        if (!itemService.checkItemExists(bookingDto.getItemId()) ||
                itemService.getItemById(bookingDto.getItemId()).get().getOwner().getId() == userId.get()) {
            throw new ItemNotFoundException();
        }

        if (!itemService.getItemById(bookingDto.getItemId()).get().isAvailable()) {
            throw new ItemNotAvailableException();
        }

        if (bookingDto.getEnd().isBefore(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault())) ||
                bookingDto.getEnd().isBefore(bookingDto.getStart()) ||
                bookingDto.getStart().isBefore(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()))) {
            throw new BookingValidationException();
        }

        return true;
    }

    @Override
    public Booking setApprovalStatus(Optional<Long> userId, long bookingId, Boolean status) {
        if (userId.isEmpty()) {
            throw new UserValidationException();
        }

        if (bookingRepository.findOneById(bookingId).getStatus().equals(APPROVED)) {
            throw new BookingAlreadyApprovedException();
        }

        if (status) {
            return bookingRepository.findById(bookingId).
                    filter(booking -> booking.getItem().getOwner().getId() == userId.get()).
                    map(booking -> {
                        booking.setStatus(APPROVED);
                        return bookingRepository.save(booking);
                    }).
                    orElseThrow(BookingNotFoundException::new);
        } else {
            return bookingRepository.findById(bookingId).
                    filter(booking -> booking.getItem().getOwner().getId() == userId.get()).
                    map(booking -> {
                        booking.setStatus(REJECTED);
                        return bookingRepository.save(booking);
                    }).
                    orElseThrow(BookingNotFoundException::new);
        }
    }

    @Override
    public Booking getBooking(Optional<Long> userId, long bookingId) {
        if (userId.isEmpty()) {
            throw new UserValidationException();
        }

        return bookingRepository.findById(bookingId).
                filter(booking -> booking.getItem().getOwner().getId() == userId.get() ||
                        booking.getBooker().getId() == userId.get()).
                orElseThrow(BookingNotFoundException::new);
    }

    private boolean checkStatusExists(String status) {
        boolean exists = true;
        try {
            BookingStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            exists = false;
        }
        return exists;
    }

    @Override
    public List<Booking> getBookingsOfUser(Optional<Long> userId, String status) {
        if (userId.isEmpty()) {
            throw new UserValidationException();
        }

        if (!checkStatusExists(status)) {
            throw new UnsupportedBookingStatusError(status);
        }

        BookingStatus state = valueOf(status);

        switch (BookingStatus.valueOf(status)) {
            case ALL:
                return bookingRepository.findByBookerEqualsOrderByStartDesc(userService.getOwner(userId.get()));
            case FUTURE:
                return bookingRepository.findByBookerEqualsAndStartAfterOrderByStartDesc(
                        userService.getOwner(userId.get()),
                        LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));
            case PAST:
                return bookingRepository.findByBookerEqualsAndEndBeforeOrderByStartDesc(
                        userService.getOwner(userId.get()),
                        LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));
            case CURRENT:
                return bookingRepository.findByBookerEqualsAndStartBeforeAndEndAfterOrderByStartDesc(
                        userService.getOwner(userId.get()),
                        LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()),
                        LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));
            default:
                return bookingRepository.findByBookerEqualsAndStatusEqualsOrderByStartDesc(
                        userService.getOwner(userId.get()), state);
        }
    }

    @Override
    public List<Booking> getBookedItemsOfUser(Optional<Long> userId, String status) {
        if (userId.isEmpty()) {
            throw new UserValidationException();
        }

        if (!checkStatusExists(status)) {
            throw new UnsupportedBookingStatusError(status);
        }

        BookingStatus state = valueOf(status);

        switch (BookingStatus.valueOf(status)) {
            case ALL:
                return bookingRepository.findByOwnerEqualsOrderByStartDesc(userService.getOwner(userId.get()));
            case FUTURE:
                return bookingRepository.findByOwnerEqualsAndStartAfterOrderByStartDesc(
                        userService.getOwner(userId.get()),
                        LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));
            case PAST:
                return bookingRepository.findByOwnerEqualsAndEndBeforeOrderByStartDesc(
                        userService.getOwner(userId.get()),
                        LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));
            case CURRENT:
                return bookingRepository.findByOwnerEqualsAndStartBeforeAndEndAfterOrderByStartDesc(
                        userService.getOwner(userId.get()),
                        LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()),
                        LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));
            default:
                return bookingRepository.findByOwnerEqualsAndStatusEqualsOrderByStartDesc(
                        userService.getOwner(userId.get()), state);
        }
    }

    public BookingDto getLastBooking(Item item) {
        List<Booking> bookings = bookingRepository.findByItemEqualsAndStartBeforeAndStatusEqualsOrderByEndDesc(
                item, LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()), APPROVED);
        if (bookings.isEmpty()) {
            return null;
        } else {
            return bookingMapper.toBookingDto(bookings.get(0));
        }
    }

    public BookingDto getNextBooking(Item item) {
        List<Booking> bookings = bookingRepository.findByItemEqualsAndStartAfterAndStatusEqualsOrderByStartDesc(
                item, LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()), APPROVED);
        if (bookings.isEmpty()) {
            return null;
        } else {
            return bookingMapper.toBookingDto(bookings.get(0));
        }
    }

    @Override
    public Object getItem(Optional<Long> userId, long itemId) {
        if (!itemService.checkItemExists(itemId)) {
            throw new ItemNotFoundException();
        }

        if (userId.isEmpty() || !userService.checkUserExists(userId.get())) {
            throw new UserValidationException();
        }

        Item item = itemService.getItemById(itemId).get();

        if (userId.get() == item.getOwner().getId()) {
            return itemService.getItemForOwner(item, getLastBooking(item), getNextBooking(item));
        } else {
            return itemService.getItemForOwner(item, null, null);
        }
    }

    public List<OwnerItemDto> getAllItems(Optional<Long> userId) {
        if (userId.isEmpty() || !userService.checkUserExists(userId.get())) {
            throw new UserValidationException();
        }

        List<Item> allItems = itemService.getAllItems(userService.getOwner(userId.get()));
        List<OwnerItemDto> allOwnerDtoItems = new ArrayList<>();

        for (Item i : allItems) {
            allOwnerDtoItems.add(itemService.convertToOwnerItemDto(i, getLastBooking(i), getNextBooking(i)));
        }

        return allOwnerDtoItems;
    }

    @Override
    public void checkUserBookedItem(long itemId, Optional<Long> userId) {
        if (userId.isEmpty() || !userService.checkUserExists(userId.get())) {
            throw new UserValidationException();
        }
        Item item = itemService.getItemById(itemId).get();
        User booker = userService.getOwner(userId.get());

        List<Booking> bookings = bookingRepository.findByBookerEqualsAndItemEqualsAndStatusEqualsAndStartBefore(
                booker, item, APPROVED, LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));
        if (bookings.isEmpty()) {
            throw new CommentsCreationIsNotAvailable();
        }
    }
}

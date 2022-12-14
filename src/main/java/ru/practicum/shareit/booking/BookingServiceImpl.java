package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
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
            booking.setOwner
                    (userService.getOwner(itemService.getItemById(bookingDto.getItemId()).get().getOwner().getId()));
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

        if (!(userId.get() == bookingRepository.findOneById(bookingId).getItem().getOwner().getId())) {
            throw new BookingNotFoundException();
        }

        if (bookingRepository.findOneById(bookingId).getStatus().equals(APPROVED)) {
            throw new BookingAlreadyApprovedException();
        }

        if (bookingRepository.existsById(bookingId)) {
            Booking booking = bookingRepository.findById(bookingId).get();
            if (status) {
                booking.setStatus(APPROVED);
            } else {
                booking.setStatus(REJECTED);
            }
            return bookingRepository.save(booking);
        } else {
            throw new BookingNotFoundException();
        }
    }

    @Override
    public Booking getBooking(Optional<Long> userId, long bookingId) {
        if (userId.isEmpty()) {
            throw new UserValidationException();
        }

        if (bookingRepository.existsById(bookingId) &&
                (bookingRepository.findOneById(bookingId).getBooker().getId() == userId.get() ||
                        bookingRepository.findOneById(bookingId).getItem().getOwner().getId() == userId.get()
                )) {
            return bookingRepository.findOneById(bookingId);
        } else {
            throw new BookingNotFoundException();
        }
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
            throw new BookingWrongStatusException();
        }

        BookingStatus state = valueOf(status);
        LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());

        switch (BookingStatus.valueOf(status)) {
            case ALL:
                return bookingRepository.findByBookerEqualsOrderByStartDesc(userService.getOwner(userId.get()));
            case FUTURE:
                return bookingRepository.findByBookerEqualsAndStartAfterOrderByStartDesc
                        (userService.getOwner(userId.get()), now);
            case PAST:
                return bookingRepository.findByBookerEqualsAndEndBeforeOrderByStartDesc
                        (userService.getOwner(userId.get()), now);
            default:
                return bookingRepository.findByBookerEqualsAndStatusEqualsOrderByStartDesc
                        (userService.getOwner(userId.get()), state);
        }
    }

    @Override
    public List<Booking> getBookedItemsOfUser(Optional<Long> userId, String status) {
        if (userId.isEmpty()) {
            throw new UserValidationException();
        }

        if (!checkStatusExists(status)) {
            throw new BookingWrongStatusException();
        }

        BookingStatus state = valueOf(status);
        LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());

        switch (BookingStatus.valueOf(status)) {
            case ALL:
                return bookingRepository.findByOwnerEqualsOrderByStartDesc(userService.getOwner(userId.get()));
            case FUTURE:
                return bookingRepository.findByOwnerEqualsAndStartAfterOrderByStartDesc
                        (userService.getOwner(userId.get()), now);
            case PAST:
                return bookingRepository.findByOwnerEqualsAndEndBeforeOrderByStartDesc
                        (userService.getOwner(userId.get()), now);
            default:
                return bookingRepository.findByOwnerEqualsAndStatusEqualsOrderByStartDesc
                        (userService.getOwner(userId.get()), state);
        }
    }

    public BookingDto getLastBooking(Item item) {
        LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        if (bookingRepository.findOneByItemEqualsAndEndBeforeOrderByEndDesc(item, now) == null) {
            return null;
        } else {
            //now = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
            return bookingMapper.toBookingDto
                    (bookingRepository.findOneByItemEqualsAndStartBeforeAndStatusEqualsOrderByEndDesc(item, now, APPROVED));
        }
    }

    public BookingDto getNextBooking(Item item) {
        LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        if (bookingRepository.findOneByItemEqualsAndStartAfterOrderByStartDesc(item, now) == null) {
            return null;
        } else {
            //now = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
            return bookingMapper.toBookingDto
                    (bookingRepository.findOneByItemEqualsAndStartAfterAndStatusEqualsOrderByStartDesc(item, now, APPROVED));
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
        User user = userService.getOwner(userId.get());

        if (userId.get() == item.getOwner().getId()){
            return itemService.getItemForOwner(item, getLastBooking(item), getNextBooking(item));
        }
        else if (checkItemBookedByUser(item, user)){
            return itemService.getItemForOwner(item, null, null);
        }
        throw new ItemNotFoundException();
    }
    private boolean checkItemBookedByUser(Item item, User user) {
        return !(bookingRepository.findByItemEqualsAndBookerEquals(item, user).isEmpty());
    }

    public List <OwnerItemDto> getAllItems(Optional<Long> userId){
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
}
package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemNotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.OwnerItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.PagingValidationException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    @Mock
    private BookingMapper bookingMapper;

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void addNewBookingWhenDtoValid() {
        Item item = new Item();
        item.setAvailable(true);

        User user = new User();
        user.setId(2L);

        item.setOwner(user);

        LocalDateTime now = LocalDateTime.ofInstant(
                Instant.now(), ZoneId.systemDefault());

        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(2L);
        bookingDto.setStart(now.plusHours(100));
        bookingDto.setEnd(now.plusHours(200));

        Optional<Long> id = Optional.of(1L);

        when(userService.checkUserExists(anyLong())).thenReturn(true);
        when(bookingMapper.toBooking(bookingDto)).thenReturn(new Booking());
        when(userService.getOwner(anyLong())).thenReturn(new User());
        when(itemService.getItemById(any())).thenReturn(Optional.of(item));
        when(itemService.checkItemExists(any())).thenReturn(true);

        bookingService.addNewBooking(id, bookingDto);

        verify(bookingRepository).save(any());
    }

    @Test
    void newBookingNotCreatedAsUserIdIsEmpty() {
        LocalDateTime now = LocalDateTime.ofInstant(
                Instant.now(), ZoneId.systemDefault());
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(2L);
        bookingDto.setStart(now.plusHours(100));
        bookingDto.setEnd(now.plusHours(200));

        Optional<Long> id = Optional.empty();

        assertThrows(UserNotFoundException.class,
                () -> bookingService.addNewBooking(id, bookingDto));


    }

    @Test
    void newBookingNotCreatedAsUserIdNotExists() {
        LocalDateTime now = LocalDateTime.ofInstant(
                Instant.now(), ZoneId.systemDefault());
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(2L);
        bookingDto.setStart(now.plusHours(100));
        bookingDto.setEnd(now.plusHours(200));
        Optional<Long> id = Optional.of(1L);

        when(userService.checkUserExists(anyLong())).thenReturn(false);

        assertThrows(UserNotFoundException.class,
                () -> bookingService.addNewBooking(id, bookingDto));
    }

    @Test
    void newBookingNotCreatedAsItemNotExists() {
        LocalDateTime now = LocalDateTime.ofInstant(
                Instant.now(), ZoneId.systemDefault());
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(2L);
        bookingDto.setStart(now.plusHours(100));
        bookingDto.setEnd(now.plusHours(200));
        Optional<Long> id = Optional.of(1L);

        when(userService.checkUserExists(anyLong())).thenReturn(true);
        when(itemService.checkItemExists(any())).thenReturn(false);

        assertThrows(ItemNotFoundException.class,
                () -> bookingService.addNewBooking(id, bookingDto));
    }

    @Test
    void newBookingNotCreatedAsItemIsUserProperty() {
        LocalDateTime now = LocalDateTime.ofInstant(
                Instant.now(), ZoneId.systemDefault());
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(2L);
        bookingDto.setStart(now.plusHours(100));
        bookingDto.setEnd(now.plusHours(200));

        Optional<Long> id = Optional.of(1L);
        Item item = new Item();
        User user = new User();
        user.setId(1L);
        item.setOwner(user);


        when(userService.checkUserExists(anyLong())).thenReturn(true);
        when(itemService.checkItemExists(any())).thenReturn(true);
        when(itemService.getItemById(any())).thenReturn(Optional.of(item));

        assertThrows(ItemNotFoundException.class,
                () -> bookingService.addNewBooking(id, bookingDto));
    }

    @Test
    void newBookingNotCreatedAaItemNotAvailable() {
        LocalDateTime now = LocalDateTime.ofInstant(
                Instant.now(), ZoneId.systemDefault());
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(2L);
        bookingDto.setStart(now.plusHours(100));
        bookingDto.setEnd(now.plusHours(200));

        Optional<Long> id = Optional.of(1L);
        Item item = new Item();
        User user = new User();
        user.setId(2L);
        item.setOwner(user);
        item.setAvailable(false);


        when(userService.checkUserExists(anyLong())).thenReturn(true);
        when(itemService.checkItemExists(any())).thenReturn(true);
        when(itemService.getItemById(any())).thenReturn(Optional.of(item));

        assertThrows(ItemNotAvailableException.class,
                () -> bookingService.addNewBooking(id, bookingDto));
    }

    @Test
    void newBookingNotCreatedAsBookingStartInPast() {
        LocalDateTime now = LocalDateTime.ofInstant(
                Instant.now(), ZoneId.systemDefault());
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(2L);
        bookingDto.setStart(now.minusHours(1));
        bookingDto.setEnd(now.plusHours(200));

        Optional<Long> id = Optional.of(1L);
        Item item = new Item();
        User user = new User();
        user.setId(2L);
        item.setOwner(user);
        item.setAvailable(true);


        when(userService.checkUserExists(anyLong())).thenReturn(true);
        when(itemService.checkItemExists(any())).thenReturn(true);
        when(itemService.getItemById(any())).thenReturn(Optional.of(item));

        assertThrows(BookingValidationException.class,
                () -> bookingService.addNewBooking(id, bookingDto));
    }

    @Test
    void newBookingNotCreatedAsBookingEndInPast() {
        LocalDateTime now = LocalDateTime.ofInstant(
                Instant.now(), ZoneId.systemDefault());
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(2L);
        bookingDto.setStart(now.plusHours(100));
        bookingDto.setEnd(now.minusHours(2));

        Optional<Long> id = Optional.of(1L);
        Item item = new Item();
        User user = new User();
        user.setId(2L);
        item.setOwner(user);
        item.setAvailable(true);


        when(userService.checkUserExists(anyLong())).thenReturn(true);
        when(itemService.checkItemExists(any())).thenReturn(true);
        when(itemService.getItemById(any())).thenReturn(Optional.of(item));

        assertThrows(BookingValidationException.class,
                () -> bookingService.addNewBooking(id, bookingDto));
    }

    @Test
    void newBookingNotCreatedAsBookingEndIsEarlierThanStart() {
        LocalDateTime now = LocalDateTime.ofInstant(
                Instant.now(), ZoneId.systemDefault());
        BookingDto bookingDto = new BookingDto();
        bookingDto.setItemId(2L);
        bookingDto.setStart(now.plusHours(100));
        bookingDto.setEnd(now.plusHours(50));

        Optional<Long> id = Optional.of(1L);
        Item item = new Item();
        User user = new User();
        user.setId(2L);
        item.setOwner(user);
        item.setAvailable(true);


        when(userService.checkUserExists(anyLong())).thenReturn(true);
        when(itemService.checkItemExists(any())).thenReturn(true);
        when(itemService.getItemById(any())).thenReturn(Optional.of(item));

        assertThrows(BookingValidationException.class,
                () -> bookingService.addNewBooking(id, bookingDto));
    }

    @Test
    void setApprovalStatusWhenUserIdEmpty() {
        Optional<Long> userId = Optional.empty();
        Long bookingId = 1L;

        assertThrows(UserValidationException.class,
                () -> bookingService.setApprovalStatus(userId, bookingId, true));

    }

    @Test
    void setApprovalStatusWhenStatusApproved() {
        Optional<Long> userId = Optional.of(1L);
        Long bookingId = 1L;
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findOneById(anyLong())).thenReturn(booking);

        assertThrows(BookingAlreadyApprovedException.class,
                () -> bookingService.setApprovalStatus(userId, bookingId, true));
    }

    @Test
    void setApprovalStatusWhenNoSuchBooking() {
        Optional<Long> userId = Optional.of(1L);
        Long bookingId = 1L;
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.WAITING);
        when(bookingRepository.findOneById(anyLong())).thenReturn(booking);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class,
                () -> bookingService.setApprovalStatus(userId, bookingId, true));
    }

    @Test
    void setApprovalStatusWhenNoSuchBookingAndStatusFalse() {
        Optional<Long> userId = Optional.of(1L);
        Long bookingId = 1L;
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.WAITING);
        when(bookingRepository.findOneById(anyLong())).thenReturn(booking);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class,
                () -> bookingService.setApprovalStatus(userId, bookingId, false));
    }

    @Test
    void getBookingNotExist() {
        Optional<Long> userId = Optional.of(1L);
        Long bookingId = 1L;

        assertThrows(BookingNotFoundException.class,
                () -> bookingService.getBooking(userId, bookingId));
    }

    @Test
    void getBookingWhenUserIdEmpty() {
        Optional<Long> userId = Optional.empty();
        Long bookingId = 1L;

        assertThrows(UserValidationException.class,
                () -> bookingService.getBooking(userId, bookingId));
    }

    @Test
    void checkStatusExistsTest() {
        String status = "APPROVED";

        assertTrue(bookingService.checkStatusExists(status));
    }

    @Test
    void checkStatusWrongTest() {
        String status = "WRONG";

        assertFalse(bookingService.checkStatusExists(status));
    }

    @Test
    void getBookingsOfUserWithEmptyUserId() {
        Optional<Long> userId = Optional.empty();
        String status = "APPROVED";
        Integer from = 0;
        Integer size = 5;

        assertThrows(UserValidationException.class,
                () -> bookingService.getBookingsOfUser(userId, status, from, size));
    }

    @Test
    void getBookingsOfUserWithWrongPageRequest() {
        Optional<Long> userId = Optional.of(1L);
        String status = "APPROVED";
        Integer from = null;
        Integer size = 5;

        List<Booking> list = bookingService.getBookingsOfUser(
                userId, status, from, size);

        assertTrue(list.isEmpty());
    }

    @Test
    void getBookingsOfUserWithInvalidPageRequest() {
        Optional<Long> userId = Optional.of(1L);
        String status = "APPROVED";
        Integer from = -5;
        Integer size = 5;

        assertThrows(PagingValidationException.class,
                () -> bookingService.getBookingsOfUser(userId, status, from, size));
    }

    @Test
    void getBookingsOfUserWithInvalidStatus() {
        Optional<Long> userId = Optional.of(1L);
        String status = "WRONG";
        Integer from = 5;
        Integer size = 5;

        assertThrows(UnsupportedBookingStatusError.class,
                () -> bookingService.getBookingsOfUser(userId, status, from, size));
    }

    @Test
    void getBookingsOfUserWithStatusAll() {
        Optional<Long> userId = Optional.of(1L);
        String status = "ALL";
        Integer from = 5;
        Integer size = 5;

        PageRequest pr = PageRequest.of((from / size), size);

        when(bookingRepository.findByBookerEqualsOrderByStartDesc(
                userService.getOwner(userId.get()), pr)).thenReturn(new ArrayList<>());

        bookingService.getBookingsOfUser(userId, status, from, size);

        verify(bookingRepository).findByBookerEqualsOrderByStartDesc(any(), any());
    }

    @Test
    void getBookingsOfUserWithStatusFuture() {
        Optional<Long> userId = Optional.of(1L);
        String status = "FUTURE";
        Integer from = 5;
        Integer size = 5;

        when(userService.getOwner(anyLong())).thenReturn(new User());
        when(bookingRepository.findByBookerEqualsAndStartAfterOrderByStartDesc(
                any(), any(), any())).thenReturn(new ArrayList<>());

        bookingService.getBookingsOfUser(userId, status, from, size);

        verify(bookingRepository).findByBookerEqualsAndStartAfterOrderByStartDesc(any(), any(), any());
    }

    @Test
    void getBookingsOfUserWithStatusPast() {
        Optional<Long> userId = Optional.of(1L);
        String status = "PAST";
        Integer from = 5;
        Integer size = 5;

        when(userService.getOwner(anyLong())).thenReturn(new User());
        when(bookingRepository.findByBookerEqualsAndEndBeforeOrderByStartDesc(
                any(), any(), any())).thenReturn(new ArrayList<>());

        bookingService.getBookingsOfUser(userId, status, from, size);

        verify(bookingRepository).findByBookerEqualsAndEndBeforeOrderByStartDesc(any(), any(), any());
    }

    @Test
    void getBookingsOfUserWithStatusCurrent() {
        Optional<Long> userId = Optional.of(1L);
        String status = "CURRENT";
        Integer from = 5;
        Integer size = 5;

        when(userService.getOwner(anyLong())).thenReturn(new User());
        when(bookingRepository.findByBookerEqualsAndStartBeforeAndEndAfterOrderByStartDesc(
                any(), any(), any(), any())).thenReturn(new ArrayList<>());

        bookingService.getBookingsOfUser(userId, status, from, size);

        verify(bookingRepository).findByBookerEqualsAndStartBeforeAndEndAfterOrderByStartDesc(any(), any(), any(), any());
    }

    @Test
    void getBookingsOfUserWithStatusDefault() {
        Optional<Long> userId = Optional.of(1L);
        String status = "WAITING";
        Integer from = 5;
        Integer size = 5;

        when(userService.getOwner(anyLong())).thenReturn(new User());
        when(bookingRepository.findByBookerEqualsAndStatusEqualsOrderByStartDesc(
                any(), any(), any())).thenReturn(new ArrayList<>());

        bookingService.getBookingsOfUser(userId, status, from, size);

        verify(bookingRepository).findByBookerEqualsAndStatusEqualsOrderByStartDesc(any(), any(), any());
    }

    @Test
    void getBookedItemsOfUserWhenUserIdEmpty() {
        Optional<Long> userId = Optional.empty();
        String status = "APPROVED";
        Integer from = 0;
        Integer size = 5;

        assertThrows(UserValidationException.class,
                () -> bookingService.getBookedItemsOfUser(userId, status, from, size));
    }

    @Test
    void getBookedItemsOfUserWithWrongPageRequest() {
        Optional<Long> userId = Optional.of(1L);
        String status = "APPROVED";
        Integer from = null;
        Integer size = 5;

        List<Booking> list = bookingService.getBookedItemsOfUser(
                userId, status, from, size);

        assertTrue(list.isEmpty());
    }

    @Test
    void getBBookedItemsOfUserWithInvalidPageRequest() {
        Optional<Long> userId = Optional.of(1L);
        String status = "APPROVED";
        Integer from = -5;
        Integer size = 5;

        assertThrows(PagingValidationException.class,
                () -> bookingService.getBookedItemsOfUser(userId, status, from, size));
    }

    @Test
    void getBookedItemsOfUserWithInvalidStatus() {
        Optional<Long> userId = Optional.of(1L);
        String status = "WRONG";
        Integer from = 5;
        Integer size = 5;

        assertThrows(UnsupportedBookingStatusError.class,
                () -> bookingService.getBookedItemsOfUser(userId, status, from, size));
    }

    @Test
    void getBookedItemsOfUserWithStatusAll() {
        Optional<Long> userId = Optional.of(1L);
        String status = "ALL";
        Integer from = 0;
        Integer size = 5;

        PageRequest pr = PageRequest.of((from / size), size);

        when(bookingRepository.findByOwnerEqualsOrderByStartDesc(
                userService.getOwner(userId.get()), pr)).thenReturn(new ArrayList<>());

        bookingService.getBookedItemsOfUser(userId, status, from, size);

        verify(bookingRepository).findByOwnerEqualsOrderByStartDesc(any(), any());
    }

    @Test
    void getBookedItemsOfUserWithStatusFuture() {
        Optional<Long> userId = Optional.of(1L);
        String status = "FUTURE";
        Integer from = 0;
        Integer size = 5;

        when(userService.getOwner(anyLong())).thenReturn(new User());
        when(bookingRepository.findByOwnerEqualsAndStartAfterOrderByStartDesc(
                any(), any(), any())).thenReturn(new ArrayList<>());

        bookingService.getBookedItemsOfUser(userId, status, from, size);

        verify(bookingRepository).findByOwnerEqualsAndStartAfterOrderByStartDesc(any(), any(), any());
    }

    @Test
    void getBookedItemsOfUserWithStatusPast() {
        Optional<Long> userId = Optional.of(1L);
        String status = "PAST";
        Integer from = 0;
        Integer size = 5;

        when(userService.getOwner(anyLong())).thenReturn(new User());
        when(bookingRepository.findByOwnerEqualsAndEndBeforeOrderByStartDesc(
                any(), any(), any())).thenReturn(new ArrayList<>());

        bookingService.getBookedItemsOfUser(userId, status, from, size);

        verify(bookingRepository).findByOwnerEqualsAndEndBeforeOrderByStartDesc(any(), any(), any());
    }

    @Test
    void getBookedItemsOfUserWithStatusCurrent() {
        Optional<Long> userId = Optional.of(1L);
        String status = "CURRENT";
        Integer from = 5;
        Integer size = 5;

        when(userService.getOwner(anyLong())).thenReturn(new User());
        when(bookingRepository.findByOwnerEqualsAndStartBeforeAndEndAfterOrderByStartDesc(
                any(), any(), any(), any())).thenReturn(new ArrayList<>());

        bookingService.getBookedItemsOfUser(userId, status, from, size);

        verify(bookingRepository).findByOwnerEqualsAndStartBeforeAndEndAfterOrderByStartDesc(any(), any(), any(), any());
    }

    @Test
    void getBookedItemsOfUserWithStatusDefault() {
        Optional<Long> userId = Optional.of(1L);
        String status = "WAITING";
        Integer from = 5;
        Integer size = 5;

        when(userService.getOwner(anyLong())).thenReturn(new User());
        when(bookingRepository.findByOwnerEqualsAndStatusEqualsOrderByStartDesc(
                any(), any(), any())).thenReturn(new ArrayList<>());

        bookingService.getBookedItemsOfUser(userId, status, from, size);

        verify(bookingRepository).findByOwnerEqualsAndStatusEqualsOrderByStartDesc(any(), any(), any());
    }

    @Test
    void getLastBookingWithNotBookingFound() {

        Item item = new Item();

        when(bookingRepository.findByItemEqualsAndStartBeforeAndStatusEqualsOrderByEndDesc(
                any(), any(), any()
        )).thenReturn(new ArrayList<>());

        Object result = bookingService.getLastBooking(item);

        assertNull(result);
    }

    @Test
    void getLastBookingWithBookingFound() {

        Item item = new Item();
        List<Booking> list = List.of(new Booking());
        BookingDto bookingDto = new BookingDto();

        when(bookingRepository.findByItemEqualsAndStartBeforeAndStatusEqualsOrderByEndDesc(
                any(), any(), any()
        )).thenReturn(list);
        when(bookingMapper.toBookingDto(list.get(0))).thenReturn(bookingDto);

        BookingDto result = bookingService.getLastBooking(item);

        assertEquals(result, bookingDto);
    }

    @Test
    void getNextBookingWithNotBookingFound() {

        Item item = new Item();

        when(bookingRepository.findByItemEqualsAndStartAfterAndStatusEqualsOrderByStartDesc(
                any(), any(), any()
        )).thenReturn(new ArrayList<>());

        Object result = bookingService.getNextBooking(item);

        assertNull(result);
    }

    @Test
    void getNextBookingWithBookingFound() {

        Item item = new Item();
        List<Booking> list = List.of(new Booking());
        BookingDto bookingDto = new BookingDto();

        when(bookingRepository.findByItemEqualsAndStartAfterAndStatusEqualsOrderByStartDesc(
                any(), any(), any()
        )).thenReturn(list);
        when(bookingMapper.toBookingDto(list.get(0))).thenReturn(bookingDto);

        BookingDto result = bookingService.getNextBooking(item);

        assertEquals(result, bookingDto);
    }

    @Test
    void getItemWithNoItemExists() {
        Optional<Long> userId = Optional.of(1L);
        Long itemId = 1L;

        when(itemService.checkItemExists(anyLong())).thenReturn(false);

        assertThrows(ItemNotFoundException.class,
                () -> bookingService.getItem(userId, itemId));
    }

    @Test
    void getItemWithNoEmptyUserId() {
        Optional<Long> userId = Optional.empty();
        Long itemId = 1L;

        when(itemService.checkItemExists(anyLong())).thenReturn(true);

        assertThrows(UserValidationException.class,
                () -> bookingService.getItem(userId, itemId));
    }

    @Test
    void getItemWithNoUserExists() {
        Optional<Long> userId = Optional.of(1L);
        Long itemId = 1L;

        when(itemService.checkItemExists(anyLong())).thenReturn(true);
        when(userService.checkUserExists(anyLong())).thenReturn(false);

        assertThrows(UserValidationException.class,
                () -> bookingService.getItem(userId, itemId));
    }

    @Test
    void getItemWithWhenOwnerRequested() {
        Optional<Long> userId = Optional.of(1L);
        Long itemId = 1L;

        Item item = new Item();
        User user = new User();
        user.setId(1L);
        item.setOwner(user);

        when(itemService.checkItemExists(anyLong())).thenReturn(true);
        when(userService.checkUserExists(anyLong())).thenReturn(true);
        when(itemService.getItemById(1L)).thenReturn(Optional.of(item));

        bookingService.getItem(userId, itemId);

        verify(itemService).getItemForOwner(any(), any(), any());

    }

    @Test
    void getItemWithWhenNotOwnerRequested() {
        Optional<Long> userId = Optional.of(2L);
        Long itemId = 1L;

        Item item = new Item();
        User user = new User();
        user.setId(1L);
        item.setOwner(user);

        when(itemService.checkItemExists(anyLong())).thenReturn(true);
        when(userService.checkUserExists(anyLong())).thenReturn(true);
        when(itemService.getItemById(1L)).thenReturn(Optional.of(item));

        bookingService.getItem(userId, itemId);

        verify(itemService).getItemForOwner(any(), any(), any());

    }

    @Test
    void getAllItemsWithEmptyUserId() {
        Optional<Long> userId = Optional.empty();
        Integer from = 5;
        Integer size = 5;

        assertThrows(UserValidationException.class,
                () -> bookingService.getAllItems(userId, from, size));
    }

    @Test
    void getAllItemsWithNoUserExists() {
        Optional<Long> userId = Optional.of(1L);
        Integer from = 5;
        Integer size = 5;

        when(userService.checkUserExists(anyLong())).thenReturn(false);

        assertThrows(UserValidationException.class,
                () -> bookingService.getAllItems(userId, from, size));
    }

    @Test
    void getAllItemsTest() {
        Optional<Long> userId = Optional.of(1L);
        Integer from = 5;
        Integer size = 5;
        Item item1 = new Item();
        Item item2 = new Item();
        List<Item> items = List.of(item1, item2);

        when(userService.checkUserExists(anyLong())).thenReturn(true);
        when(itemService.getAllItems(any(), any(), any())).thenReturn(items);
        when(itemService.convertToOwnerItemDto(any(), any(), any())).thenReturn(new OwnerItemDto());

        List<OwnerItemDto> result = bookingService.getAllItems(userId, from, size);

        assertFalse(result.isEmpty());
    }


    @Test
    void checkUserBookedItemWhenUserIdIsEmpty() {
        Optional<Long> userId = Optional.empty();
        Long itemId = 1L;

        assertThrows(UserValidationException.class,
                () -> bookingService.checkUserBookedItem(itemId, userId));
    }

    @Test
    void checkUserBookedItemWhenNoBookingsMade() {
        Optional<Long> userId = Optional.of(1L);
        Long itemId = 1L;
        Item item = new Item();
        User user = new User();
        List<Booking> result = new ArrayList<>();

        when(userService.checkUserExists(userId.get())).thenReturn(true);
        when(itemService.getItemById(itemId)).thenReturn(Optional.of(item));
        when(userService.getOwner(userId.get())).thenReturn(user);
        when(bookingRepository.findByBookerEqualsAndItemEqualsAndStatusEqualsAndStartBefore(
                any(), any(), any(), any()))
                .thenReturn(result);


        assertThrows(CommentsCreationIsNotAvailable.class,
                () -> bookingService.checkUserBookedItem(itemId, userId));
    }
}
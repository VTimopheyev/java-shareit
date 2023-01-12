package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.internal.matchers.Null;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemNotFoundException;
import ru.practicum.shareit.item.ItemService;
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
    BookingRepository bookingRepository;

    @Mock
    UserService userService;

    @Mock
    ItemService itemService;

    @Mock
    BookingMapper bookingMapper;

    @InjectMocks
    BookingServiceImpl bookingService;

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
    void setApprovalStatusWhenUserIdEmpty (){
        Optional<Long> userId = Optional.empty();
        Long bookingId = 1L;

        assertThrows(UserValidationException.class,
                () -> bookingService.setApprovalStatus(userId, bookingId, true));

    }

    @Test
    void setApprovalStatusWhenStatusApproved (){
        Optional<Long> userId = Optional.of(1L);
        Long bookingId = 1L;
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.APPROVED);
        when(bookingRepository.findOneById(anyLong())).thenReturn(booking);

        assertThrows(BookingAlreadyApprovedException.class,
                () -> bookingService.setApprovalStatus(userId, bookingId, true));
    }

    @Test
    void setApprovalStatusWhenNoSuchBooking (){
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
    void setApprovalStatusWhenNoSuchBookingAndStatusFalse (){
        Optional<Long> userId = Optional.of(1L);
        Long bookingId = 1L;
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.WAITING);
        when(bookingRepository.findOneById(anyLong())).thenReturn(booking);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class,
                () -> bookingService.setApprovalStatus(userId, bookingId, false));
    }

    /*@Test
    void setApprovalStatusWhenBookingExists (){
        Optional<Long> userId = Optional.of(1L);
        Long bookingId = 1L;
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.WAITING);
        User user = new User();
        user.setId(1L);

        when(bookingRepository.findOneById(anyLong())).thenReturn(booking);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(BookingNotFoundException.class,
                () -> bookingService.setApprovalStatus(userId, bookingId, true));
    }*/


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
    void checkStatusExistsTest(){
        String status = "APPROVED";

        assertTrue(bookingService.checkStatusExists(status));
    }

    @Test
    void checkStatusWrongTest(){
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
                userId, status,from, size);

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
    void getBookedItemsOfUser() {
    }

    @Test
    void getLastBooking() {
    }

    @Test
    void getNextBooking() {
    }

    @Test
    void getItem() {
    }

    @Test
    void getAllItems() {
    }

    @Test
    void checkUserBookedItem() {
    }
}
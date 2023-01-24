package ru.practicum.shareit.booking;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
class BookingRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    User user1;
    User user2;
    Item item1;

    Item item2;
    Booking booking1;
    Booking booking2;

    @BeforeEach
    public void createInstances() {
        User user1 = new User();
        user1.setName("User1");
        user1.setEmail("user1@mail.com");
        user1 = userRepository.save(user1);
        this.user1 = user1;

        User user2 = new User();
        user2.setName("User2");
        user2.setEmail("user2@mail.com");
        user2 = userRepository.save(user2);
        this.user2 = user2;

        Item item1 = new Item();
        item1.setName("Item1");
        item1.setDescription("Some description");
        item1.setAvailable(true);
        item1.setOwner(user1);
        item1 = itemRepository.save(item1);
        this.item1 = item1;

        Item item2 = new Item();
        item2.setName("Item2");
        item2.setDescription("Some another description");
        item2.setAvailable(true);
        item2.setOwner(user2);
        item2 = itemRepository.save(item2);
        this.item2 = item2;

        Booking booking1 = new Booking();
        booking1.setItem(item1);
        booking1.setBooker(user2);
        booking1.setOwner(user1);
        booking1.setStart(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()).plusHours(24));
        booking1.setEnd(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()).plusHours(124));
        booking1.setStatus(BookingStatus.WAITING);
        booking1 = bookingRepository.save(booking1);
        this.booking1 = booking1;

        Booking booking2 = new Booking();
        booking2.setItem(item2);
        booking2.setBooker(user1);
        booking2.setOwner(user2);
        booking2.setStart(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()).minusHours(124));
        booking2.setEnd(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()).minusHours(24));
        booking2.setStatus(BookingStatus.PAST);
        booking2 = bookingRepository.save(booking2);
        this.booking2 = booking2;
    }


    @Test
    void findByBookerEqualsOrderByStartDesc() {

        PageRequest pr = PageRequest.of(0, 5);
        List<Booking> list = bookingRepository.findByBookerEqualsOrderByStartDesc(
                user2, pr);

        assertFalse(list.isEmpty());
    }

    @Test
    void findByBookerEqualsAndStatusEqualsOrderByStartDesc() {
        PageRequest pr = PageRequest.of(0, 5);
        List<Booking> list = bookingRepository.findByBookerEqualsAndStatusEqualsOrderByStartDesc(
                user2, BookingStatus.WAITING, pr);

        assertFalse(list.isEmpty());
    }

    @Test
    void findOneById() {
        Booking b = bookingRepository.findOneById(15L);
        assertEquals(booking1, b);
    }

    @Test
    void findByBookerEqualsAndStartAfterOrderByStartDesc() {
        PageRequest pr = PageRequest.of(0, 5);
        LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        List<Booking> list = bookingRepository.findByBookerEqualsAndStartAfterOrderByStartDesc(
                user2, now, pr);

        assertFalse(list.isEmpty());
    }

    @Test
    void findByBookerEqualsAndEndBeforeOrderByStartDesc() {
        PageRequest pr = PageRequest.of(0, 5);
        LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        List<Booking> list = bookingRepository.findByBookerEqualsAndEndBeforeOrderByStartDesc(
                user1, now, pr);

        assertFalse(list.isEmpty());
    }

    @Test
    void findByOwnerEqualsOrderByStartDesc() {
        PageRequest pr = PageRequest.of(0, 5);
        List<Booking> list = bookingRepository.findByOwnerEqualsOrderByStartDesc(
                user1, pr);

        assertFalse(list.isEmpty());
    }

    @Test
    void findByOwnerEqualsAndStartAfterOrderByStartDesc() {
        PageRequest pr = PageRequest.of(0, 5);
        LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        List<Booking> list = bookingRepository.findByOwnerEqualsAndStartAfterOrderByStartDesc(
                user1, now, pr);

        assertFalse(list.isEmpty());
    }

    @Test
    void findByOwnerEqualsAndEndBeforeOrderByStartDesc() {
        PageRequest pr = PageRequest.of(0, 5);
        LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        List<Booking> list = bookingRepository.findByOwnerEqualsAndEndBeforeOrderByStartDesc(
                user2, now, pr);

        assertFalse(list.isEmpty());
    }

    @Test
    void findByOwnerEqualsAndStatusEqualsOrderByStartDesc() {
        PageRequest pr = PageRequest.of(0, 5);
        List<Booking> list = bookingRepository.findByOwnerEqualsAndStatusEqualsOrderByStartDesc(
                user2, BookingStatus.PAST, pr);

        assertFalse(list.isEmpty());
    }

    @Test
    void findByItemEqualsAndStartBeforeAndStatusEqualsOrderByEndDesc() {

        booking2.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking1);
        LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        List<Booking> list = bookingRepository.findByItemEqualsAndStartBeforeAndStatusEqualsOrderByEndDesc(
                item2, now, BookingStatus.APPROVED);

        assertFalse(list.isEmpty());
    }

    @Test
    void findByItemEqualsAndStartAfterAndStatusEqualsOrderByStartDesc() {
        LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        List<Booking> list = bookingRepository.findByItemEqualsAndStartAfterAndStatusEqualsOrderByStartDesc(
                item1, now, BookingStatus.WAITING);

        assertFalse(list.isEmpty());
    }

    @Test
    void findByBookerEqualsAndItemEqualsAndStatusEqualsAndStartBefore() {
        LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        List<Booking> list = bookingRepository.findByBookerEqualsAndItemEqualsAndStatusEqualsAndStartBefore(
                user1, item2, BookingStatus.PAST, now);

        assertFalse(list.isEmpty());
    }

    @Test
    void findByOwnerEqualsAndStartBeforeAndEndAfterOrderByStartDesc() {
        LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        booking1.setStart(now.minusHours(24));
        bookingRepository.save(booking1);
        PageRequest pr = PageRequest.of(0, 5);
        List<Booking> list = bookingRepository.findByOwnerEqualsAndStartBeforeAndEndAfterOrderByStartDesc(
                user1, now, now, pr);

        assertFalse(list.isEmpty());
    }

    @Test
    void findByBookerEqualsAndStartBeforeAndEndAfterOrderByStartDesc() {
        LocalDateTime now = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
        booking1.setStart(now.minusHours(24));
        bookingRepository.save(booking1);
        PageRequest pr = PageRequest.of(0, 5);
        List<Booking> list = bookingRepository.findByBookerEqualsAndStartBeforeAndEndAfterOrderByStartDesc(
                user2, now, now, pr);

        assertFalse(list.isEmpty());
    }

    @AfterEach
    public void deleteInstances() {
        userRepository.deleteAll();
        itemRepository.deleteAll();
        bookingRepository.deleteAll();
    }
}
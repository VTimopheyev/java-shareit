package ru.practicum.shareit.booking;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.repository.PagingAndSortingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends PagingAndSortingRepository<Booking, Long> {
    List<Booking> findByBookerEqualsOrderByStartDesc(User booker, PageRequest pr);

    List<Booking> findByBookerEqualsAndStatusEqualsOrderByStartDesc(User booker, BookingStatus status, PageRequest pr);

    Booking findOneById(long bookingId);

    List<Booking> findByBookerEqualsAndStartAfterOrderByStartDesc(User owner, LocalDateTime ofInstant, PageRequest pr);

    List<Booking> findByBookerEqualsAndEndBeforeOrderByStartDesc(User owner, LocalDateTime now, PageRequest pr);

    List<Booking> findByOwnerEqualsOrderByStartDesc(User owner, PageRequest pr);

    List<Booking> findByOwnerEqualsAndStartAfterOrderByStartDesc(User owner, LocalDateTime now, PageRequest pr);

    List<Booking> findByOwnerEqualsAndEndBeforeOrderByStartDesc(User owner, LocalDateTime now, PageRequest pr);

    List<Booking> findByOwnerEqualsAndStatusEqualsOrderByStartDesc(User owner, BookingStatus state, PageRequest pr);

    List<Booking> findByItemEqualsAndStartBeforeAndStatusEqualsOrderByEndDesc(
            Item item, LocalDateTime now, BookingStatus approved);

    List<Booking> findByItemEqualsAndStartAfterAndStatusEqualsOrderByStartDesc(
            Item item, LocalDateTime now, BookingStatus approved);

    List<Booking> findByBookerEqualsAndItemEqualsAndStatusEqualsAndStartBefore(
            User booker, Item item, BookingStatus approved, LocalDateTime now);

    List<Booking> findByOwnerEqualsAndStartBeforeAndEndAfterOrderByStartDesc(
            User owner, LocalDateTime now1, LocalDateTime now2, PageRequest pr);

    List<Booking> findByBookerEqualsAndStartBeforeAndEndAfterOrderByStartDesc(
            User booker, LocalDateTime ofInstant, LocalDateTime now, PageRequest pr);
}

package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerEqualsOrderByStartDesc(User booker);

    List<Booking> findByBookerEqualsAndStatusEqualsOrderByStartDesc(User booker, BookingStatus status);

    Booking findOneById(long bookingId);

    List<Booking> findByBookerEqualsAndStartAfterOrderByStartDesc(User owner, LocalDateTime ofInstant);

    List<Booking> findByBookerEqualsAndEndBeforeOrderByStartDesc(User owner, LocalDateTime now);

    List<Booking> findByOwnerEqualsOrderByStartDesc(User owner);

    List<Booking> findByOwnerEqualsAndStartAfterOrderByStartDesc(User owner, LocalDateTime now);

    List<Booking> findByOwnerEqualsAndEndBeforeOrderByStartDesc(User owner, LocalDateTime now);

    List<Booking> findByOwnerEqualsAndStatusEqualsOrderByStartDesc(User owner, BookingStatus state);

    List<Booking> findByItemEqualsAndStartBeforeAndStatusEqualsOrderByEndDesc(
            Item item, LocalDateTime now, BookingStatus approved);

    List<Booking> findByItemEqualsAndStartAfterAndStatusEqualsOrderByStartDesc(
            Item item, LocalDateTime now, BookingStatus approved);

    List<Booking> findByBookerEqualsAndItemEqualsAndStatusEqualsAndStartBefore(
            User booker, Item item, BookingStatus approved, LocalDateTime now);

    List<Booking> findByOwnerEqualsAndStartBeforeAndEndAfterOrderByStartDesc(
            User owner, LocalDateTime now1, LocalDateTime now2);

    List<Booking> findByBookerEqualsAndStartBeforeAndEndAfterOrderByStartDesc(
            User booker, LocalDateTime ofInstant, LocalDateTime now);
}

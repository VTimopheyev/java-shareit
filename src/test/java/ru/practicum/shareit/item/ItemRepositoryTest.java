package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    User user1;
    User user2;
    Item item1;

    Item item2;

    ItemRequest itemRequest;

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

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setDescription("Some description");
        itemRequest.setCreated(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));
        itemRequest.setUser(user2);
        itemRequestRepository.save(itemRequest);
        this.itemRequest = itemRequest;

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
        item2.setRequest(itemRequest);
        item2 = itemRepository.save(item2);
        this.item2 = item2;

    }

    @Test
    void findAllByOwner() {
        PageRequest pr = PageRequest.of(0, 5);
        List<Item> items = itemRepository.findAllByOwner(user1, pr);

        assertFalse(items.isEmpty());
        assertEquals(1, items.size());
    }

    @Test
    void findByNameContainingOrDescriptionContainingIgnoreCase() {
        PageRequest pr = PageRequest.of(0, 5);
        String text = "another";

        List<Item> items = itemRepository.findByNameContainingOrDescriptionContainingIgnoreCase(
                text, text, pr);

        assertFalse(items.isEmpty());
        assertEquals(1, items.size());
    }

    @Test
    void findByRequestEquals() {
        List<Item> items = itemRepository.findByRequestEquals(itemRequest);

        assertFalse(items.isEmpty());
        assertEquals(item2, items.get(0));
    }
}
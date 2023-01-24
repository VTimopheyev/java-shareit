package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@DataJpaTest
class ItemRequestRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    User user1;
    User user2;
    Item item1;

    ItemRequest itemRequest;
    ItemRequest itemRequest2;

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

    }

    @Test
    void findByUserEqualsOrderByCreatedDesc() {
        Collection<ItemRequest> list = itemRequestRepository.findByUserEqualsOrderByCreatedDesc(user2);

        assertFalse(list.isEmpty());
        assertEquals(1, list.size());

    }

    @Test
    void findAllByUserNot() {
        PageRequest pr = PageRequest.of(0, 5);
        List<ItemRequest> list = itemRequestRepository.findAllByUserNot(user1, pr);

        assertFalse(list.isEmpty());
        assertEquals(1, list.size());
    }
}
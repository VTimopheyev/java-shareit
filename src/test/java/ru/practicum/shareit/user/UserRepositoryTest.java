package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class UserRepositoryTest {

    @Autowired
    UserRepository userRepository;
    User user1;
    User user2;


    @Test
    void findOneById() {
        User user1 = new User();
        user1.setName("User1");
        user1.setEmail("user1@mail.com");
        user1 = userRepository.save(user1);
        System.out.println(user1);

        User user = userRepository.findOneById(Optional.of(user1.getId()));

        assertEquals(user, user1);
    }
}
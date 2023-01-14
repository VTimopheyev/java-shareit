package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    UserRepository userRepository;
    @Mock
    UserMapper userMapper;
    @InjectMocks
    UserServiceImpl userService;

    @Test
    void createUserWhenUserEmailNotValid() {
        UserDto userDto = new UserDto();
        User user = new User();
        user.setEmail("someemail");

        when(userMapper.toUser(userDto)).thenReturn(user);

        assertThrows(UserValidationException.class,
                () -> userService.create(userDto));
    }

    @Test
    void createUserWhenUserNamelNotValid() {
        UserDto userDto = new UserDto();
        User user = new User();
        user.setEmail("somee@mail.com");
        user.setName("");

        when(userMapper.toUser(userDto)).thenReturn(user);

        assertThrows(UserValidationException.class,
                () -> userService.create(userDto));
    }

    @Test
    void createUserTest() {
        UserDto userDto = new UserDto();
        User user = new User();
        user.setEmail("somee@mail.com");
        user.setName("Bob");

        when(userMapper.toUser(userDto)).thenReturn(user);

        userService.create(userDto);

        verify(userRepository).save(user);
    }

    @Test
    void getAllTest() {
        User user = new User();
        user.setId(1L);
        user.setName("Bob");
        user.setEmail("bob@email.com");

        User user2 = new User();
        user2.setId(2L);
        user2.setName("Todd");
        user2.setEmail("todd@email.com");

        when(userRepository.findAll()).thenReturn(List.of(user, user2));

        List<UserDto> result = userService.getAll();

        assertEquals(2, result.size());
    }

    @Test
    void updateUserWhenUserNotExist() {
        UserDto userDto = new UserDto();
        Long id = 1L;

        when(userRepository.findAll()).thenReturn(new ArrayList<>());

        assertThrows(UserNotFoundException.class,
                () -> userService.update(id, userDto));

    }

    @Test
    void updateUserWhenEmailAlreadyExists() {
        UserDto userDto = new UserDto();
        Long id = 1L;

        when(userRepository.findAll()).thenReturn(List.of(new User()));

        assertThrows(UserNotFoundException.class,
                () -> userService.update(id, userDto));

    }

    @Test
    void updateUserTest() {
        UserDto userDto = new UserDto();
        User user = new User();
        user.setId(1L);
        user.setEmail("todd@email.com");
        userDto.setName("Bob");
        userDto.setEmail("bob@email.com");
        Long id = 1L;

        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userRepository.findById(id)).thenReturn(Optional.of(user));

        userService.update(id, userDto);

        verify(userRepository).save(user);
    }

    @Test
    void getUserTest() {
        Long id = 1L;

        assertThrows(UserNotFoundException.class,
                () -> userService.getUser(id));
    }

    @Test
    void deleteUserWhenUserNotExists() {
        Long id = 1L;

        when(userRepository.findAll()).thenReturn(new ArrayList<>());

        assertThrows(UserNotFoundException.class,
                () -> userService.deleteUser(id));
    }

    @Test
    void deleteUserTest() {
        Long id = 1L;

        assertThrows(UserNotFoundException.class,
                () -> userService.deleteUser(id));
    }

    @Test
    void getOwner() {
        Long id = 1L;

        when(userRepository.findById(id)).thenReturn(Optional.of(new User()));
        userService.getOwner(id);

        verify(userRepository).findById(id);
    }
}
package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserMapperTest {
    @InjectMocks
    private UserMapper userMapper;

    @Test
    void toUserDto() {
        User user = new User();
        user.setId(1L);
        user.setName("Bon");
        user.setEmail("bob@email.com");

        UserDto userDto = userMapper.toUserDto(user);

        assertNotNull(userDto);
    }

    @Test
    void toUser() {
        UserDto userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("Bon");
        userDto.setEmail("bob@email.com");

        User user = userMapper.toUser(userDto);

        assertNotNull(user);
    }
}
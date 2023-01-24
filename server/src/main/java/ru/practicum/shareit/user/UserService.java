package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {

    UserDto create(UserDto userDto);

    List<UserDto> getAll();

    UserDto update(Long id, UserDto userDto);

    UserDto getUser(long id);

    void deleteUser(long userId);

    boolean validateUser(User user);

    boolean checkEmailExists(String email);

    boolean checkUserExists(long userId);

    User getOwner(Long aLong);
}

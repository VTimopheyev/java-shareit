package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    @Qualifier("UserStorageInMemoryImpl")
    private final UserStorage userStorage;
    private final UserMapper userMapper;

    @Override
    public UserDto create(UserDto userDto) {

        User user = userMapper.toUser(userDto);
        checkEmailExists(user.getEmail());

        if (!validateUser(user)) {
            throw new UserValidationException();
        }

        return userMapper.toUserDto(userStorage.create(user));
    }

    @Override
    public List<UserDto> getAll() {
        List<User> allUsers = userStorage.getAll();
        List<UserDto> allUsersDto = new ArrayList<>();
        for (User u : allUsers) {
            allUsersDto.add(userMapper.toUserDto(u));
        }

        return allUsersDto;
    }

    @Override
    public UserDto update(long id, UserDto userDto) {
        User user = userMapper.toUser(userDto);
        if (!checkUserExists(id) || checkEmailExists(user.getEmail())) {
            throw new UserNotFoundException();
        }
        return userMapper.toUserDto(userStorage.update(id, user));
    }

    @Override
    public UserDto getUser(long id) {
        return userMapper.toUserDto(userStorage.getUser(id));
    }

    @Override
    public void deleteUser(long userId) {
        if (!checkUserExists(userId)) {
            throw new UserNotFoundException();
        }
        userStorage.deleteUser(userId);
    }

    @Override
    public boolean validateUser(User user) {

        if (user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            return false;
        } else if (user.getName().isEmpty() || user.getName().contains(" ")) {
            return false;
        }

        return true;
    }

    @Override
    public boolean checkEmailExists(String email) {
        List<User> users = userStorage.getAll();
        for (User u : users) {
            if (u.getEmail().equals(email)) {
                throw new UserEmailInvalidException();
            }
        }
        return false;
    }

    @Override
    public boolean checkUserExists(long userId) {
        List<User> users = userStorage.getAll();
        if (!users.isEmpty()) {
            for (User u : users) {
                if (u.getId() == userId) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public User getOwner(Long id) {
        return userStorage.getUser(id);
    }
}

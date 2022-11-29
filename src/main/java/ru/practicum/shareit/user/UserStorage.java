package ru.practicum.shareit.user;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    User create(User user);

    User update(long id, User user);

    User getUser(long id);

    void deleteUser(long userId);

    List<User> getAll();
}

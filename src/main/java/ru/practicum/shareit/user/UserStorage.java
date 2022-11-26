package ru.practicum.shareit.user;

import java.util.List;

public interface UserStorage {
    User create(User user);

    User update(long id, User user);

    User getUser(long id);

    void deleteUser(long userId);

    List<User> getAll();

    boolean checkEmailExists(String email);

    boolean checkUserExists(long userId);
}

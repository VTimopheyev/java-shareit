package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    @Qualifier("UserStorageInMemoryImp")
    private final UserStorage userStorage;

    public User create(User user) {

        userStorage.checkEmailExists(user.getEmail());

        if (!validateUser(user)) {
            throw new UserValidationException("User credentials are invalid");
        }

        return userStorage.create(user);
    }

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public User update(long id, User user) {
        if (!userStorage.checkUserExists(id)) {
            throw new UserNotFoundException("No such user");
        }
        return userStorage.update(id, user);
    }

    public User getUser(long id) {
        return userStorage.getUser(id);
    }

    public void deleteUser(long userId) {
        if (!userStorage.checkUserExists(userId)) {
            throw new UserNotFoundException("User not found");
        }
        userStorage.deleteUser(userId);
    }

    private boolean validateUser(User user) {

        if (user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            return false;
        } else if (user.getName().isEmpty() || user.getName().contains(" ")) {
            return false;
        }

        return true;
    }
}

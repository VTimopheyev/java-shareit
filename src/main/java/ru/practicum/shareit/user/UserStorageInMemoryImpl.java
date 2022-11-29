package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class UserStorageInMemoryImpl implements UserStorage {

    private Map<Long, User> users = new HashMap<>();
    private long idCount = 1;


    @Override
    public User create(User user) {
        user.setId(idCount);
        users.put(user.getId(), user);
        idCount++;

        return getUser(user.getId());
    }

    @Override
    public User update(long id, User user) {
        User updatedUser = getUser(id);
        if (user.getEmail() != null) {
            updatedUser.setEmail(user.getEmail());
        }

        if (user.getName() != null) {
            updatedUser.setName(user.getName());
        }
        users.remove(id);
        users.put(id, updatedUser);
        return users.get(id);
    }

    @Override
    public User getUser(long id) {
        return users.get(id);
    }

    @Override
    public void deleteUser(long userId) {
        users.remove(userId);
    }

    @Override
    public List<User> getAll() {
        List<User> allUsers = new ArrayList<>();
        for (Long id : users.keySet()) {
            allUsers.add(users.get(id));
        }
        return allUsers;
    }
}

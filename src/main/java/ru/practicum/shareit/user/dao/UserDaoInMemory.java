package ru.practicum.shareit.user.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class UserDaoInMemory implements UserDao {
    private static long uid = 0;
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User addUser(User user) {
        user.setId(++uid);

        users.put(uid, user);

        log.debug("User: {} added", user);

        return user;
    }

    @Override
    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUserById(long id) {
        return getUserOrThrowException(id);
    }

    @Override
    public boolean isEmailExists(String email) {
        return users.values().stream()
                .anyMatch(u -> u.getEmail().equals(email));
    }

    @Override
    public User updateUser(User user) {
        User userInMemory = getUserOrThrowException(user.getId());

        log.debug("User for update: {}, fields for update: {}", userInMemory, user);

        if (user.getName() != null) {
            userInMemory.setName(user.getName());
        }
        if (user.getEmail() != null) {
            userInMemory.setEmail(user.getEmail());
        }

        log.debug("User after update: {}", userInMemory);

        return userInMemory;
    }

    @Override
    public void deleteUser(long userId) {
        users.remove(userId);

        log.debug("User with id = {} deleted", userId);
    }

    private User getUserOrThrowException(long id) {
        if (!users.containsKey(id)) {
            throw new EntityNotFoundException(String.format("Пользователь с ID = %d не найден", id));
        }

        return users.get(id);
    }
}

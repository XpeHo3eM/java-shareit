package ru.practicum.shareit.user.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Slf4j
@Repository
public class UserDaoInMemory implements UserDao {
    private static long uid = 0;
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();

    @Override
    public User addUser(User user) {
        user.setId(++uid);

        users.put(uid, user);
        emails.add(user.getEmail());

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
        return emails.contains(email);
    }

    @Override
    public User updateUser(User user) {
        User userInMemory = getUserOrThrowException(user.getId());

        log.debug("User for update: {}, fields for update: {}", userInMemory, user);

        if (user.getName() != null) {
            userInMemory.setName(user.getName());
        }
        if (user.getEmail() != null) {
            emails.remove(userInMemory.getEmail());

            String userEmail = user.getEmail();

            userInMemory.setEmail(userEmail);
            emails.add(userEmail);
        }

        log.debug("User after update: {}", userInMemory);

        return userInMemory;
    }

    @Override
    public void deleteUser(long userId) {
        User removedUser = users.remove(userId);

        if (removedUser != null) {
            emails.remove(removedUser.getEmail());
        }

        log.debug("User with id = {} deleted", userId);
    }

    private User getUserOrThrowException(long id) {
        if (!users.containsKey(id)) {
            throw new EntityNotFoundException(String.format("Пользователь с ID = %d не найден", id));
        }

        return users.get(id);
    }
}

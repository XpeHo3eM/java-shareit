package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserDao {
    User addUser(User user);

    List<User> getAllUsers();

    User getUserById(long id);

    boolean isEmailExists(String email);

    User updateUser(User user);

    void deleteUser(long userId);
}

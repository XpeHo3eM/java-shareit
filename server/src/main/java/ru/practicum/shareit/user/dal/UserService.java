package ru.practicum.shareit.user.dal;

import ru.practicum.shareit.user.dto.CreatingUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto addUser(CreatingUserDto creatingUserDto);

    UserDto getUserById(long id);

    List<UserDto> getAllUsers();

    UserDto updateUser(long userId, CreatingUserDto creatingUserDto);

    void deleteUser(long id);
}

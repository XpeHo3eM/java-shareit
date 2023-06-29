package ru.practicum.shareit.user.dal;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Mapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao storage;

    @Override
    public UserDto addUser(UserDto userDto) {
        if (storage.isEmailExists(userDto.getEmail())) {
            throw new EmailAlreadyExistsException(String.format("Пользователь с email = %s уже существует", userDto.getEmail()));
        }

        User user = Mapper.toUser(userDto);

        return Mapper.userToDto(storage.addUser(user));
    }

    @Override
    public UserDto getUserById(long id) {
        return Mapper.userToDto(storage.getUserById(id));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return storage.getAllUsers().stream()
                .map(Mapper::userToDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        storage.getUserById(userDto.getId());

        if (!isSameEmail(userDto) && storage.isEmailExists(userDto.getEmail())) {
            throw new EmailAlreadyExistsException(String.format("Пользователь с email = %s уже существует", userDto.getEmail()));
        }

        User user = Mapper.toUser(userDto);

        return Mapper.userToDto(storage.updateUser(user));
    }

    @Override
    public void deleteUser(long id) {
        storage.deleteUser(id);
    }

    private boolean isSameEmail(UserDto userDto) {
        String userEmailOnDb = storage.getUserById(userDto.getId()).getEmail();

        return userEmailOnDb.equals(userDto.getEmail());
    }
}

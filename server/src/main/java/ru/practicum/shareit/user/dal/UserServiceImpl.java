package ru.practicum.shareit.user.dal;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.CreatingUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository repository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto addUser(CreatingUserDto creatingUserDto) {
        User user = userMapper.toUser(creatingUserDto);

        return save(user);
    }

    @Override
    public UserDto getUserById(long id) {
        return userMapper.toDto(getUserOrThrowException(id));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return repository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto updateUser(long userId, CreatingUserDto creatingUserDto) {
        User userInRepository = getUserOrThrowException(userId);
        User user = userMapper.toUser(creatingUserDto);

        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            userInRepository.setEmail(user.getEmail());
        }
        if (user.getName() != null && !user.getName().isBlank()) {
            userInRepository.setName(user.getName());
        }

        return save(userInRepository);
    }

    @Override
    @Transactional
    public void deleteUser(long id) {
        getUserOrThrowException(id);

        repository.deleteById(id);
    }

    private User getUserOrThrowException(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с ID = %d не найден", id)));
    }

    private UserDto save(User user) {
        try {
            return userMapper.toDto(repository.saveAndFlush(user));
        } catch (DataIntegrityViolationException e) {
            throw new AlreadyExistsException(String.format("Пользователь с email: %s уже существует", user.getEmail()));
        }
    }
}

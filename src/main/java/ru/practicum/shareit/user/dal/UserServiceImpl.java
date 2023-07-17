package ru.practicum.shareit.user.dal;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.CreatingUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository repository;

    @Override
    @Transactional
    public UserDto addUser(CreatingUserDto creatingUserDto) {
        User user = UserMapper.toUser(creatingUserDto);

        return save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getUserById(long id) {
        return UserMapper.toDto(getUserOrThrowException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return repository.findAll().stream()
                .map(UserMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto updateUser(long userId, CreatingUserDto creatingUserDto) {
        User userInRepository = getUserOrThrowException(userId);
        User user = UserMapper.toUser(creatingUserDto);

        if (user.getEmail() != null) {
            userInRepository.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            userInRepository.setName(user.getName());
        }

        return save(userInRepository);
    }

    @Override
    @Transactional
    public void deleteUser(long id) {
        repository.deleteById(id);
    }

    private User getUserOrThrowException(long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с ID = %d не найден", id)));
    }

    private UserDto save(User user) {
        try {
            return UserMapper.toDto(repository.saveAndFlush(user));
        } catch (DataIntegrityViolationException e) {
            throw new AlreadyExistsException(String.format("Пользователь с email: %s уже существует", user.getEmail()));
        }
    }
}

package ru.practicum.shareit.user.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.CreatingUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceTest {
    private static UserMapper userMapper;
    private static UserRepository repository;
    private static UserService service;
    private final CreatingUserDto creatingUserDto = CreatingUserDto.builder()
            .name("user")
            .email("user@email.com")
            .build();
    private final User user = User.builder()
            .id(1L)
            .name("user1")
            .email("user1@email.com")
            .build();
    private final UserDto userDto = UserDto.builder()
            .id(1L)
            .name("user1")
            .email("user1@email.com")
            .build();
    private final User user2 = User.builder()
            .id(2L)
            .name("user2")
            .email("user2@email.com")
            .build();

    @BeforeEach
    void initialize() {
        repository = Mockito.mock(UserRepository.class);
        userMapper = Mockito.mock(UserMapper.class);
        service = new UserServiceImpl(repository, userMapper);
    }

    @Test
    void shouldAddUser() {
        when(userMapper.toUser(any(CreatingUserDto.class)))
            .thenReturn(user);
        when(repository.saveAndFlush(any(User.class)))
                .thenReturn(user);
        when(userMapper.toDto(any(User.class)))
                .thenReturn(userDto);

        UserDto userFromService = service.addUser(creatingUserDto);

        assertThat(userFromService)
                .hasFieldOrPropertyWithValue("id", user.getId())
                .hasFieldOrPropertyWithValue("name", user.getName())
                .hasFieldOrPropertyWithValue("email", user.getEmail());
        verify(repository, times(1)).saveAndFlush(any(User.class));
        verify(userMapper, times(1)).toUser(any(CreatingUserDto.class));
        verify(userMapper, times(1)).toDto(any(User.class));
    }

    @Test
    void shouldThrowExceptionByAddingIncorrectUser() {
        when(userMapper.toUser(any(CreatingUserDto.class)))
                .thenReturn(user);
        when(repository.saveAndFlush(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("error"));

        assertThrows(AlreadyExistsException.class, () -> service.addUser(creatingUserDto));
        verify(userMapper, times(1)).toUser(any(CreatingUserDto.class));
        verify(repository, times(1)).saveAndFlush(any(User.class));
    }

    @Test
    void shouldGetUserById() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(userMapper.toDto(any(User.class)))
                .thenReturn(userDto);

        UserDto userFromService = service.getUserById(user.getId());

        assertThat(userFromService)
                .hasFieldOrPropertyWithValue("id", user.getId())
                .hasFieldOrPropertyWithValue("name", user.getName())
                .hasFieldOrPropertyWithValue("email", user.getEmail());
        verify(repository, times(1)).findById(anyLong());
        verify(userMapper, times(1)).toDto(any(User.class));
    }

    @Test
    void shouldThrowExceptionByGetUserWithIncorrectId() {
        assertThrows(EntityNotFoundException.class, () -> service.getUserById(666L));

        verify(repository, times(1)).findById(anyLong());
    }

    @Test
    void shouldGetAllUsers() {
        when(repository.findAll())
                .thenReturn(List.of(user, user2));
        when(userMapper.toDto(any(User.class)))
                .thenReturn(userDto);

        List<UserDto> usersFromService = service.getAllUsers();

        assertThat(usersFromService)
                .hasSize(2);
        verify(repository, times(1)).findAll();
        verify(userMapper, times(2)).toDto(any(User.class));
    }

    @Test
    void shouldThrowExceptionByUpdateUserWithIncorrectId() {
        assertThrows(EntityNotFoundException.class, () -> service.updateUser(666L, creatingUserDto));

        verify(repository, times(1)).findById(anyLong());
    }

    @Test
    void shouldUpdateUserName() {
        CreatingUserDto updateNameDto = CreatingUserDto.builder()
                .name("updated")
                .build();
        UserDto udpatedUserDto = UserDto.builder()
                .id(userDto.getId())
                .name(updateNameDto.getName())
                .email(userDto.getEmail())
                .build();

        when(repository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(userMapper.toUser(any(CreatingUserDto.class)))
                .thenReturn(user);
        when(repository.saveAndFlush(any(User.class)))
                .thenReturn(user.toBuilder().name(updateNameDto.getName()).build());
        when(userMapper.toDto(any(User.class)))
                .thenReturn(udpatedUserDto);

        UserDto userFromServiceWithUpdatedName = service.updateUser(user.getId(), updateNameDto);
        assertThat(userFromServiceWithUpdatedName)
                .hasFieldOrPropertyWithValue("id", user.getId())
                .hasFieldOrPropertyWithValue("name", updateNameDto.getName())
                .hasFieldOrPropertyWithValue("email", user.getEmail());
        verify(repository, times(1)).saveAndFlush(any(User.class));
    }

    @Test
    void shouldUpdateUserEmail() {
        CreatingUserDto updateEmailDto = CreatingUserDto.builder()
                .email("updated@gmail.com")
                .build();
        UserDto udpatedUserDto = UserDto.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .email(updateEmailDto.getEmail())
                .build();

        when(repository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(userMapper.toUser(any(CreatingUserDto.class)))
                .thenReturn(user);
        when(repository.saveAndFlush(any(User.class)))
                .thenReturn(user.toBuilder().email(updateEmailDto.getEmail()).build());
        when(userMapper.toDto(any(User.class)))
                .thenReturn(udpatedUserDto);

        UserDto userFromServiceWithUpdatedEmail = service.updateUser(user.getId(), updateEmailDto);

        assertThat(userFromServiceWithUpdatedEmail)
                .hasFieldOrPropertyWithValue("id", user.getId())
                .hasFieldOrPropertyWithValue("name", user.getName())
                .hasFieldOrPropertyWithValue("email", updateEmailDto.getEmail());
        verify(repository, times(1)).saveAndFlush(any(User.class));
    }

    @Test
    void shouldThrowExceptionByDeleteUserWithIncorrectId() {
        assertThrows(EntityNotFoundException.class, () -> service.deleteUser(666L));

        verify(repository, times(1)).findById(anyLong());
    }

    @Test
    void shouldDeleteUser() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.of(user));

        service.deleteUser(user.getId());

        verify(repository, times(1)).deleteById(anyLong());
    }
}
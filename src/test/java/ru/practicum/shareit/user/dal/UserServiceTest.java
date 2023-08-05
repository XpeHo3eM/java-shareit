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
@DirtiesContext
public class UserServiceTest {
    private static UserRepository repository;
    private static UserService service;
    private static final CreatingUserDto creatingUserDto = CreatingUserDto.builder()
            .name("user")
            .email("user@email.com")
            .build();
    private static final User user1 = User.builder()
            .id(1L)
            .name("user1")
            .email("user1@email.com")
            .build();
    private static final User user2 = User.builder()
            .id(2L)
            .name("user2")
            .email("user2@email.com")
            .build();

    @BeforeEach
    void initialize() {
        repository = Mockito.mock(UserRepository.class);
        service = new UserServiceImpl(repository);
    }

    @Test
    void shouldAddUser() {
        when(repository.saveAndFlush(any(User.class)))
                .thenReturn(user1);

        UserDto userFromService = service.addUser(creatingUserDto);

        assertThat(userFromService)
                .hasFieldOrPropertyWithValue("id", user1.getId())
                .hasFieldOrPropertyWithValue("name", user1.getName())
                .hasFieldOrPropertyWithValue("email", user1.getEmail());
        verify(repository, times(1)).saveAndFlush(any(User.class));
    }

    @Test
    void shouldThrowExceptionByAddingIncorrectUser() {
        when(repository.saveAndFlush(any(User.class)))
                .thenThrow(new DataIntegrityViolationException("error"));

        assertThrows(AlreadyExistsException.class, () -> service.addUser(creatingUserDto));
        verify(repository, times(1)).saveAndFlush(any(User.class));
    }

    @Test
    void shouldGetUserById() {
        when(repository.findById(anyLong()))
                .thenReturn(Optional.of(user1));

        UserDto userFromService = service.getUserById(user1.getId());

        assertThat(userFromService)
                .hasFieldOrPropertyWithValue("id", user1.getId())
                .hasFieldOrPropertyWithValue("name", user1.getName())
                .hasFieldOrPropertyWithValue("email", user1.getEmail());
        verify(repository, times(1)).findById(anyLong());
    }

    @Test
    void shouldThrowExceptionByGetUserWithIncorrectId() {
        assertThrows(EntityNotFoundException.class, () -> service.getUserById(666L));

        verify(repository, times(1)).findById(anyLong());
    }

    @Test
    void shouldGetAllUsers() {
        when(repository.findAll())
                .thenReturn(List.of(user1, user2));

        List<UserDto> usersFromService = service.getAllUsers();

        UserDto user1Dto = UserDto.builder()
                .id(user1.getId())
                .name(user1.getName())
                .email(user1.getEmail())
                .build();
        UserDto user2Dto = UserDto.builder()
                .id(user2.getId())
                .name(user2.getName())
                .email(user2.getEmail())
                .build();

        assertThat(usersFromService)
                .hasSize(2)
                .containsExactly(user1Dto, user2Dto);
        verify(repository, times(1)).findAll();
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

        when(repository.findById(anyLong()))
                .thenReturn(Optional.of(user1));
        when(repository.saveAndFlush(any(User.class)))
                .thenReturn(user1.toBuilder().name(updateNameDto.getName()).build());

        UserDto userFromServiceWithUpdatedName = service.updateUser(user1.getId(), updateNameDto);
        assertThat(userFromServiceWithUpdatedName)
                .hasFieldOrPropertyWithValue("id", user1.getId())
                .hasFieldOrPropertyWithValue("name", updateNameDto.getName())
                .hasFieldOrPropertyWithValue("email", user1.getEmail());
        verify(repository, times(1)).saveAndFlush(any(User.class));
    }

    @Test
    void shouldUpdateUserEmail() {
        CreatingUserDto updateEmailDto = CreatingUserDto.builder()
                .email("updated@gmail.com")
                .build();

        when(repository.findById(anyLong()))
                .thenReturn(Optional.of(user1));
        when(repository.saveAndFlush(any(User.class)))
                .thenReturn(user1.toBuilder().email(updateEmailDto.getEmail()).build());

        UserDto userFromServiceWithUpdatedEmail = service.updateUser(user1.getId(), updateEmailDto);

        assertThat(userFromServiceWithUpdatedEmail)
                .hasFieldOrPropertyWithValue("id", user1.getId())
                .hasFieldOrPropertyWithValue("name", user1.getName())
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
                .thenReturn(Optional.of(user1));

        service.deleteUser(user1.getId());

        verify(repository, times(1)).deleteById(anyLong());
    }
}
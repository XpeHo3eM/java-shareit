package ru.practicum.shareit.user.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dal.UserService;
import ru.practicum.shareit.user.dto.CreatingUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserIntegrationTest {
    private final UserService service;
    private final EntityManager entityManager;
    private final static CreatingUserDto creatingUserDto = CreatingUserDto.builder()
            .name("name")
            .email("email@ya.ru")
            .build();
    private final static CreatingUserDto creatingUserDto2 = CreatingUserDto.builder()
            .name("name2")
            .email("email2@ya.ru")
            .build();

    @Test
    void addUser() {
        service.addUser(creatingUserDto);

        User userFromService = entityManager.createQuery(getQueryGetUserById(), User.class)
                .setParameter("id", 1L)
                .getSingleResult();

        assertThat(userFromService)
                .hasFieldOrPropertyWithValue("name", creatingUserDto.getName())
                .hasFieldOrPropertyWithValue("email", creatingUserDto.getEmail());
    }

    @Test
    void getUserById() {
        service.addUser(creatingUserDto);

        UserDto userFromService = service.getUserById(1L);

        assertThat(userFromService)
                .hasFieldOrPropertyWithValue("name", creatingUserDto.getName())
                .hasFieldOrPropertyWithValue("email", creatingUserDto.getEmail());
    }

    @Test
    void getAllUsers() {
        service.addUser(creatingUserDto);
        service.addUser(creatingUserDto2);

        List<UserDto> usersFromService = service.getAllUsers();

        UserDto userDto = UserDto.builder()
                .id(1L)
                .name(creatingUserDto.getName())
                .email(creatingUserDto.getEmail())
                .build();
        UserDto userDto2 = UserDto.builder()
                .id(2L)
                .name(creatingUserDto2.getName())
                .email(creatingUserDto2.getEmail())
                .build();

        assertThat(usersFromService)
                .hasSize(2)
                .containsExactly(userDto, userDto2);
    }

    @Test
    void updateUser() {
        service.addUser(creatingUserDto);

        CreatingUserDto updatedDto = CreatingUserDto.builder()
                .name("updated")
                .build();

        service.updateUser(1L, updatedDto);

        User userFromService = entityManager.createQuery(getQueryGetUserById(), User.class)
                .setParameter("id", 1L)
                .getSingleResult();

        assertThat(userFromService)
                .hasFieldOrPropertyWithValue("name", updatedDto.getName())
                .hasFieldOrPropertyWithValue("email", creatingUserDto.getEmail());
    }

    @Test
    void deleteUser() {
        service.addUser(creatingUserDto);

        List<User> usersFromService = entityManager.createQuery(getQueryGetAllUsers(), User.class)
                .getResultList();

        assertThat(usersFromService)
                .hasSize(1);

        service.deleteUser(1L);

        usersFromService = entityManager.createQuery(getQueryGetAllUsers(), User.class)
                .getResultList();

        assertThat(usersFromService)
                .hasSize(0);
    }

    private String getQueryGetUserById() {
        return "SELECT u " +
                " FROM User AS u" +
                " WHERE u.id = :id";
    }

    private String getQueryGetAllUsers() {
        return "SELECT u " +
                " FROM User AS u";
    }
}

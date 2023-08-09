package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dal.UserService;
import ru.practicum.shareit.user.dto.CreatingUserDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.util.Constant;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    ObjectMapper mapper;
    @Autowired
    MockMvc mvc;

    @MockBean
    UserService service;

    private final CreatingUserDto creatingUserDto = CreatingUserDto.builder()
            .name("Test user")
            .email("test@email.com")
            .build();
    private final UserDto userDto = UserDto.builder()
            .id(1L)
            .name("Test user")
            .email("test@email.com")
            .build();
    private final UserDto userDto2 = UserDto.builder()
            .id(2L)
            .name("Test user 2")
            .email("test2@email.com")
            .build();

    @Test
    void shouldGetAllUsers() throws Exception {
        when(service.getAllUsers())
                .thenReturn(List.of(userDto, userDto2));

        mvc.perform(get("/users")
                        .header(Constant.HEADER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDto.getName())))
                .andExpect(jsonPath("$[0].email", is(userDto.getEmail())))
                .andExpect(jsonPath("$[1].id", is(userDto2.getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(userDto2.getName())))
                .andExpect(jsonPath("$[1].email", is(userDto2.getEmail())));
        verify(service, times(1)).getAllUsers();
    }

    @Test
    void shouldGetUserById() throws Exception {
        when(service.getUserById(anyLong()))
                .thenReturn(userDto);

        mvc.perform(get("/users/1")
                        .header(Constant.HEADER_USER_ID, 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
        verify(service, times(1)).getUserById(anyLong());
    }

    @Test
    void shouldAddUser() throws Exception {
        when(service.addUser(any(CreatingUserDto.class)))
                .thenReturn(userDto);

        mvc.perform(post("/users")
                        .header(Constant.HEADER_USER_ID, 1L)
                        .content(mapper.writeValueAsString(creatingUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
        verify(service, times(1)).addUser(creatingUserDto);
    }

    @Test
    void shouldThrowExceptionByAddingUserWithoutName() throws Exception {
        CreatingUserDto creatingUserDtoWithoutName = CreatingUserDto.builder()
                .email("email@ya.ru")
                .build();

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(creatingUserDtoWithoutName))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, never()).addUser(any(CreatingUserDto.class));
    }

    @Test
    void shouldThrowExceptionByAddingUserWithBlankName() throws Exception {
        CreatingUserDto creatingUserDtoWithBlankName = CreatingUserDto.builder()
                .name(" ")
                .build();

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(creatingUserDtoWithBlankName))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, never()).addUser(any(CreatingUserDto.class));
    }

    @Test
    void shouldThrowExceptionByAddingUserWithBlankEmail() throws Exception {
        CreatingUserDto creatingUserDtoWithBlankEmail = CreatingUserDto.builder()
                .email(" ")
                .build();

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(creatingUserDtoWithBlankEmail))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, never()).addUser(any(CreatingUserDto.class));

    }

    @Test
    void shouldThrowExceptionByAddingUserWithoutEmail() throws Exception {
        CreatingUserDto creatingUserDtoWithoutEmail = CreatingUserDto.builder()
                .name("name")
                .build();

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(creatingUserDtoWithoutEmail))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, never()).addUser(any(CreatingUserDto.class));
    }

    @Test
    void shouldThrowExceptionByAddingUserWithIncorrectEmail() throws Exception {
        CreatingUserDto creatingUserDtoWithIncorrectEmail = CreatingUserDto.builder()
                .name("name")
                .email("email.ru")
                .build();

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(creatingUserDtoWithIncorrectEmail))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, never()).addUser(any(CreatingUserDto.class));
    }

    @Test
    void shouldUpdateUser() throws Exception {
        when(service.updateUser(anyLong(), any()))
                .thenReturn(userDto);

        mvc.perform(patch("/users/1")
                        .header(Constant.HEADER_USER_ID, 1L)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
        verify(service, times(1)).updateUser(anyLong(), any(CreatingUserDto.class));
    }

    @Test
    void shouldDeleteUser() throws Exception {
        mvc.perform(delete("/users/1")
                        .header(Constant.HEADER_USER_ID, 1L))
                .andExpect(status().isOk());
        verify(service, times(1)).deleteUser(anyLong());
    }
}

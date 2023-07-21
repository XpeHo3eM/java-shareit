package ru.practicum.shareit.user.controller;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dal.UserService;
import ru.practicum.shareit.user.dto.CreatingUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
public class UserController {
    private final UserService service;

    @GetMapping
    public List<UserDto> getAllUsers() {
        return service.getAllUsers();
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable long userId) {
        return service.getUserById(userId);
    }

    @PostMapping
    public UserDto addUser(@Valid @RequestBody CreatingUserDto creatingUserDto) {
        return service.addUser(creatingUserDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable long userId,
                              @RequestBody CreatingUserDto creatingUserDto) {
        return service.updateUser(userId, creatingUserDto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteUser(@PathVariable long userId) {
        service.deleteUser(userId);
    }
}

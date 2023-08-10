package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.marker.OnCreate;
import ru.practicum.shareit.marker.OnUpdate;
import ru.practicum.shareit.user.dto.CreatingUserDto;

import javax.validation.Valid;

@Controller
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class GatewayUserController {
    private final UserClient client;

    @GetMapping
    public ResponseEntity<Object> getAllUsers() {
        return client.getAllUsers();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getUserById(@PathVariable long userId) {
        return client.getUserById(userId);
    }

    @PostMapping
    public ResponseEntity<Object> addUser(@Valid @Validated(OnCreate.class) @RequestBody CreatingUserDto creatingUserDto) {
        return client.addUser(creatingUserDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@PathVariable long userId,
                                             @RequestBody @Validated(OnUpdate.class) CreatingUserDto creatingUserDto) {
        return client.updateUser(userId, creatingUserDto);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable long userId) {
        return client.deleteUser(userId);
    }
}

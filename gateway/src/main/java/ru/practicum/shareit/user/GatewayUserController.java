package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.CreatingUserDto;
import ru.practicum.shareit.util.marker.OnCreate;
import ru.practicum.shareit.util.marker.OnUpdate;

@Controller
@RequestMapping("/users")
@RequiredArgsConstructor
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
    public ResponseEntity<Object> addUser(@RequestBody @Validated(OnCreate.class) CreatingUserDto creatingUserDto) {
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

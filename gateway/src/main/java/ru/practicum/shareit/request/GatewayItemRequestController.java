package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.CreatingItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.util.Constant.*;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class GatewayItemRequestController {
    private final RequestClient client;

    @PostMapping
    public ResponseEntity<Object> addItemRequest(@RequestHeader(HEADER_USER_ID) long userId,
                                                 @Valid @RequestBody CreatingItemRequestDto creatingItemRequestDto) {
        return client.addItemRequest(userId, creatingItemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequests(@RequestHeader(HEADER_USER_ID) long userId) {
        return client.getItemRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllItemRequests(@RequestHeader(HEADER_USER_ID) long userId,
                                                     @RequestParam(defaultValue = DEFAULT_START_PAGE) @PositiveOrZero Integer from,
                                                     @RequestParam(defaultValue = DEFAULT_SIZE_PAGE) @Positive Integer size) {
        return client.getAllItemRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequest(@RequestHeader(HEADER_USER_ID) long userId,
                                                 @PathVariable long requestId) {
        return client.getItemRequest(userId, requestId);
    }
}

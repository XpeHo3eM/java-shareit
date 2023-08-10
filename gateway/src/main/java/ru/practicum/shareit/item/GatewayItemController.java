package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.comment.CreatingCommentDto;
import ru.practicum.shareit.item.dto.item.CreatingItemDto;
import ru.practicum.shareit.marker.OnCreate;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.Collections;

import static ru.practicum.shareit.util.Constant.*;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
public class GatewayItemController {
    private final ItemClient client;

    @PostMapping
    public ResponseEntity<Object> addItem(@RequestHeader(HEADER_USER_ID) long userId,
                                          @Valid @RequestBody CreatingItemDto creatingItemDto) {
        return client.addItem(userId, creatingItemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestHeader(HEADER_USER_ID) long userId,
                                             @PathVariable long itemId,
                                             @RequestBody CreatingItemDto creatingItemDto) {
        return client.updateItem(userId, itemId, creatingItemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(HEADER_USER_ID) long userId,
                                              @PathVariable long itemId) {
        return client.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getOwnerItems(@RequestHeader(HEADER_USER_ID) long userId,
                                                @RequestParam(defaultValue = DEFAULT_START_PAGE) @PositiveOrZero Integer from,
                                                @RequestParam(defaultValue = DEFAULT_SIZE_PAGE) @Positive Integer size) {
        return client.getOwnerItems(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findItem(@RequestHeader(HEADER_USER_ID) long userId,
                                           @RequestParam String text,
                                           @RequestParam(defaultValue = DEFAULT_START_PAGE) @PositiveOrZero Integer from,
                                           @RequestParam(defaultValue = DEFAULT_SIZE_PAGE) @Positive Integer size) {
        if (text.isBlank()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        return client.findItem(userId, text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(HEADER_USER_ID) long userId,
                                             @PathVariable long itemId,
                                             @RequestBody @Valid CreatingCommentDto creatingCommentDto) {
        return client.addComment(userId, itemId, creatingCommentDto);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@RequestHeader(HEADER_USER_ID) long userId,
                       @PathVariable long itemId) {
        client.delete(userId, itemId);
    }
}

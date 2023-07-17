package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dal.ItemService;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CreatingCommentDto;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.dto.item.CreatingItemDto;

import static ru.practicum.shareit.util.Constant.HEADER_USER_ID;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService service;

    @PostMapping
    public ItemDto addItem(@RequestHeader(HEADER_USER_ID) long userId,
                           @Valid @RequestBody CreatingItemDto creatingItemDto) {
        return service.addItem(userId, creatingItemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader(HEADER_USER_ID) long userId,
                              @PathVariable long itemId,
                              @RequestBody CreatingItemDto creatingItemDto) {
        return service.updateItem(userId, itemId, creatingItemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@RequestHeader(HEADER_USER_ID) long userId,
                           @PathVariable long itemId) {
        return service.getItemById(userId, itemId);
    }

    @GetMapping
    public List<ItemDto> getOwnerItems(@RequestHeader(HEADER_USER_ID) long userId) {
        return service.getAllItemsByOwnerId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> findItem(@RequestHeader(HEADER_USER_ID) long userId,
                                  @RequestParam String text) {
        return service.search(userId, text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader(HEADER_USER_ID) long userId,
                                    @PathVariable long itemId,
                                    @RequestBody @Valid CreatingCommentDto commentDto) {
        return service.addComment(userId, itemId, commentDto);
    }
}

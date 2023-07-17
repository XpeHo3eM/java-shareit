package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusType;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.dto.item.CreatingItemDto;
import ru.practicum.shareit.item.dto.item.ItemDtoShort;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.booking.mapper.BookingMapper;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@UtilityClass
public class ItemMapper {
    public ItemDto toDto(Item item) {
        List<CommentDto> comments = new ArrayList<>();

        if (item.getComments() != null) {
            comments.addAll(item.getComments().stream()
                    .sorted(Comparator.comparing(Comment::getCreationDate).reversed())
                    .map(CommentMapper::toDto)
                    .collect(Collectors.toList()));
        }

        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .comments(comments)
                .build();
    }

    public ItemDto toDtoWithBooking(Item item) {
        LocalDateTime now = LocalDateTime.now();
        ItemDto itemDto = toDto(item);
        Set<Booking> bookings = item.getBookings();

        Optional<Booking> lastBooking = bookings.stream()
                .sorted(Comparator.comparing(Booking::getDateStart).reversed())
                .filter(b -> b.getDateStart().isBefore(now) &&
                        b.getStatus().equals(StatusType.APPROVED))
                .findFirst();

        Optional<Booking> nextBooking = bookings.stream()
                .sorted(Comparator.comparing(Booking::getDateStart))
                .filter(b -> b.getDateStart().isAfter(now) &&
                        b.getStatus().equals(StatusType.APPROVED))
                .findFirst();

        lastBooking.ifPresent(booking -> itemDto.setLastBooking(BookingMapper.toBookingDtoShort(booking)));
        nextBooking.ifPresent(booking -> itemDto.setNextBooking(BookingMapper.toBookingDtoShort(booking)));

        return itemDto;
    }

    public Item toItem(CreatingItemDto creatingItemDto) {
        return Item.builder()
                .name(creatingItemDto.getName())
                .description(creatingItemDto.getDescription())
                .available(creatingItemDto.getAvailable() != null ? creatingItemDto.getAvailable() : true)
                .build();
    }

    public ItemDtoShort toDtoShort(Item item) {
        return ItemDtoShort.builder()
                .id(item.getId())
                .name(item.getName())
                .build();
    }
}

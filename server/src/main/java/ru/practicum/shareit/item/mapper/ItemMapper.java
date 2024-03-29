package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.StatusType;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.item.CreatingItemDto;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.dto.item.ItemDtoShort;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Mapper(componentModel = "spring")
public abstract class ItemMapper {
    @Autowired
    protected CommentMapper commentMapper;
    @Autowired
    @Lazy
    protected BookingMapper bookingMapper;

    @Mapping(target = "comments", source = "item.comments", qualifiedByName = "getSortedCommentDto")
    @Mapping(target = "requestId", source = "itemRequest.id")
    public abstract ItemDto toDto(Item item);

    @Mapping(target = "comments", source = "item.comments", qualifiedByName = "getSortedCommentDto")
    @Mapping(target = "lastBooking", source = "item.bookings", qualifiedByName = "getLastBooking")
    @Mapping(target = "nextBooking", source = "item.bookings", qualifiedByName = "getNextBooking")
    @Mapping(target = "requestId", source = "itemRequest.id")
    public abstract ItemDto toDtoWithBooking(Item item);

    public abstract Item toItem(CreatingItemDto creatingItemDto);

    public abstract ItemDtoShort toDtoShort(Item item);

    @Named("getSortedCommentDto")
    List<CommentDto> getSortedCommentDto(Set<Comment> comments) {
        List<CommentDto> dtoComments = new ArrayList<>();

        if (comments != null) {
            dtoComments.addAll(comments.stream()
                    .sorted(Comparator.comparing(Comment::getCreationDate).reversed())
                    .map(commentMapper::toDto)
                    .collect(Collectors.toList()));
        }

        return dtoComments;
    }

    @Named("getLastBooking")
    BookingDtoShort getLastBooking(Set<Booking> bookings) {
        if (bookings == null) {
            return null;
        }

        return bookings.stream()
                .sorted(Comparator.comparing(Booking::getDateStart).reversed())
                .filter(b -> b.getDateStart().isBefore(LocalDateTime.now()) &&
                        b.getStatus().equals(StatusType.APPROVED))
                .map(bookingMapper::toBookingDtoShort)
                .findFirst()
                .orElse(null);
    }

    @Named("getNextBooking")
    BookingDtoShort getNextBooking(Set<Booking> bookings) {
        if (bookings == null) {
            return null;
        }

        return bookings.stream()
                .sorted(Comparator.comparing(Booking::getDateStart))
                .filter(b -> b.getDateStart().isAfter(LocalDateTime.now()) &&
                        b.getStatus().equals(StatusType.APPROVED))
                .map(bookingMapper::toBookingDtoShort)
                .findFirst()
                .orElse(null);
    }
}

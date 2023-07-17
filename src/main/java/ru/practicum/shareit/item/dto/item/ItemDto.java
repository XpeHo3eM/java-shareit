package ru.practicum.shareit.item.dto.item;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.item.dto.comment.CommentDto;

import java.util.List;

@Data
@Builder
public class ItemDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDtoShort lastBooking;
    private BookingDtoShort nextBooking;
    private List<CommentDto> comments;
}

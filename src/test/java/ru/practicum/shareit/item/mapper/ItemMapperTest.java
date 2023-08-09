package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusType;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class ItemMapperTest {
    @Autowired
    private ItemMapper mapper;

    private static final LocalDateTime now = LocalDateTime.now();
    private static final Item item = Item.builder()
            .id(1L)
            .name("name")
            .description("description")
            .available(true)
            .build();
    private static final Comment comment = Comment.builder()
            .id(1L)
            .text("comment")
            .creationDate(now.minusDays(11))
            .author(User.builder().build())
            .item(item)
            .build();
    private static final Comment comment2 = Comment.builder()
            .id(2L)
            .text("comment2")
            .creationDate(now.minusDays(4))
            .author(User.builder().build())
            .item(item)
            .build();
    private static final Booking booking = Booking.builder()
            .id(1L)
            .dateStart(now.minusDays(2))
            .dateEnd(now.minusDays(1))
            .item(item)
            .booker(User.builder().build())
            .status(StatusType.APPROVED)
            .build();
    private static final Booking booking2 = Booking.builder()
            .id(2L)
            .dateStart(now.plusDays(1))
            .dateEnd(now.plusDays(2))
            .item(item)
            .booker(User.builder().build())
            .status(StatusType.APPROVED)
            .build();

    @BeforeAll
    static void initialize() {
        item.setComments(Set.of(comment2, comment));
        item.setBookings(Set.of(booking, booking2));
    }

    @Test
    void shouldGetItemDtoWithBooking() {
        ItemDto itemDto = mapper.toDtoWithBooking(item);

        assertThat(itemDto)
                .satisfies(item ->
                        assertThat(item.getComments().get(0)).hasFieldOrPropertyWithValue("id", 2L));
    }

}

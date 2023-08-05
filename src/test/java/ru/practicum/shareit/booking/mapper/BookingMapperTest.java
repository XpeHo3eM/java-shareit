package ru.practicum.shareit.booking.mapper;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class BookingMapperTest {
    @Autowired
    private BookingMapper mapper;

    private final LocalDateTime now = LocalDateTime.now();
    private final User booker = User.builder()
            .id(1L)
            .name("booker")
            .email("email@ya.ru")
            .build();
    private final Item item = Item.builder()
            .id(1L)
            .name("item")
            .available(true)
            .owner(booker)
            .build();
    private final Booking booking = Booking.builder()
            .id(1L)
            .booker(booker)
            .item(item)
            .dateStart(now)
            .dateEnd(now.plusDays(10))
            .build();

    @Test
    void shouldConvertBookingToDtoShort() {
        BookingDtoShort bookingDtoShort = mapper.toBookingDtoShort(booking);

        assertThat(bookingDtoShort)
                .hasFieldOrPropertyWithValue("id", booking.getId())
                .hasFieldOrPropertyWithValue("bookerId", booking.getBooker().getId());
    }

    @Test
    void shouldBeEqual() {
        Booking booking2 = Booking.builder()
                .id(1L)
                .dateStart(now.minusSeconds(11))
                .dateEnd(now.plusDays(88))
                .build();

        assertEquals(booking, booking2);
    }
}

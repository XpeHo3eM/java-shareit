package ru.practicum.shareit.booking.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.StatusType;
import ru.practicum.shareit.booking.dal.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreatingBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dal.ItemService;
import ru.practicum.shareit.item.dto.item.CreatingItemDto;
import ru.practicum.shareit.user.dal.UserService;
import ru.practicum.shareit.user.dto.CreatingUserDto;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BookingIntegrationTest {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;
    private final EntityManager entityManager;

    private final CreatingUserDto userDto = CreatingUserDto.builder()
            .name("user")
            .email("email@ya.ru")
            .build();
    private final CreatingUserDto userDto2 = CreatingUserDto.builder()
            .name("user2")
            .email("email2@ya.ru")
            .build();
    private final CreatingItemDto itemDto = CreatingItemDto.builder()
            .name("item")
            .description("description")
            .available(true)
            .build();
    private final CreatingBookingDto bookingDto = CreatingBookingDto.builder()
            .itemId(1L)
            .start(LocalDateTime.now().minusDays(2))
            .end(LocalDateTime.now().minusDays(1))
            .build();
    private final CreatingItemDto itemDto2 = CreatingItemDto.builder()
            .name("item2")
            .description("description2")
            .available(true)
            .build();
    private final CreatingBookingDto bookingDto2 = CreatingBookingDto.builder()
            .itemId(2L)
            .start(LocalDateTime.now().minusDays(2))
            .end(LocalDateTime.now().minusDays(1))
            .build();
    private final PageRequest page = PageRequest.of(0, 5, Sort.by("dateStart").descending());

    @BeforeEach
    void initialize() {
        userService.addUser(userDto);
        userService.addUser(userDto2);
        itemService.addItem(1L, itemDto);
        itemService.addItem(1L, itemDto2);
        bookingService.addBooking(2L, bookingDto);
        bookingService.addBooking(2L, bookingDto2);
    }

    @Test
    void shouldCreateBooking() {
        Booking booking = entityManager.createQuery(getQueryGetBookingById(), Booking.class)
                .setParameter("id", 1L)
                .getSingleResult();

        Booking booking1 = Booking.builder()
                .id(1L)
                .build();
        assertThat(booking).isEqualTo(booking1);

        assertThat(booking)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("booker.id", 2L)
                .hasFieldOrPropertyWithValue("status", StatusType.WAITING);
    }

    @Test
    void shouldApproveBooking() {
        bookingService.approveBooking(1L, 1L, true);

        Booking booking = entityManager.createQuery(getQueryGetBookingById(), Booking.class)
                .setParameter("id", 1L)
                .getSingleResult();

        assertThat(booking)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("booker.id", 2L)
                .hasFieldOrPropertyWithValue("status", StatusType.APPROVED);
    }

    @Test
    void shouldGetBookingByUserOwner() {
        bookingService.approveBooking(1L, 1L, true);

        BookingDto bookingDto = bookingService.getBooking(1L, 1L);

        assertThat(bookingDto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("booker.id", 2L)
                .hasFieldOrPropertyWithValue("status", StatusType.APPROVED);
    }

    @Test
    void shouldGetUserBookings() {
        List<BookingDto> bookings = bookingService.getUserBookings(1L, "all", page);

        assertThat(bookings).asList().isEmpty();
    }

    @Test
    void shouldGetOwnerBookings() {
        List<BookingDto> bookings = bookingService.getOwnerBookings(1L, "all", page);

        assertThat(bookings).asList()
                .isNotEmpty()
                .hasSize(2);
    }

    private String getQueryGetBookingById() {
        return "SELECT b " +
                " FROM Booking AS b" +
                " WHERE b.id = :id";
    }
}

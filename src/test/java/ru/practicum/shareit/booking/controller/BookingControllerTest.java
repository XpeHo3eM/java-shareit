package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dal.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreatingBookingDto;
import ru.practicum.shareit.booking.model.StatusType;
import ru.practicum.shareit.item.dto.item.ItemDtoShort;
import ru.practicum.shareit.user.dto.UserDtoShort;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.util.Constant.DATE_TIME_FORMATTER;
import static ru.practicum.shareit.util.Constant.HEADER_USER_ID;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    @MockBean
    BookingService service;
    private final LocalDateTime now = LocalDateTime.now();
    private final UserDtoShort user = UserDtoShort.builder()
            .id(1L)
            .build();
    private final ItemDtoShort item = ItemDtoShort.builder()
            .id(1L)
            .name("item")
            .build();
    private final CreatingBookingDto creatingBookingDto = CreatingBookingDto.builder()
            .itemId(1L)
            .start(now.plusDays(1))
            .end(now.plusDays(2))
            .build();
    private final UserDtoShort userDtoShort = UserDtoShort.builder()
            .id(1L)
            .build();
    private final BookingDto bookingDto = BookingDto.builder()
            .id(1L)
            .item(item)
            .booker(user)
            .start(now)
            .end(now.plusDays(55))
            .status(StatusType.WAITING)
            .build();
    private final BookingDto bookingDto2 = BookingDto.builder()
            .id(2L)
            .item(item)
            .booker(user)
            .start(now.plusDays(2))
            .end(now.plusDays(5))
            .status(StatusType.APPROVED)
            .build();
    private final List<BookingDto> bookings = List.of(bookingDto, bookingDto2);

    @Test
    void shouldGetExceptionWithCreateBookingWithoutHeader() throws Exception {
        mvc.perform(post("/bookings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, never()).addBooking(anyLong(), any(CreatingBookingDto.class));
    }

    @Test
    void shouldGetExceptionWithCreateBookingWithStartInPast() throws Exception {
        CreatingBookingDto creatingBookingDto = CreatingBookingDto.builder()
                .itemId(1L)
                .start(now.minusDays(1))
                .end(now.plusDays(1))
                .build();
        String jsonBooking = mapper.writeValueAsString(creatingBookingDto);

        mvc.perform(post("/bookings")
                        .header(HEADER_USER_ID, userDtoShort.getId())
                        .content(jsonBooking)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, never()).addBooking(anyLong(), any(CreatingBookingDto.class));
    }

    @Test
    void shouldGetExceptionWithCreateBookingWithEndInPast() throws Exception {
        CreatingBookingDto creatingBookingDto = CreatingBookingDto.builder()
                .itemId(1L)
                .start(now.minusDays(2))
                .end(now.minusDays(1))
                .build();
        String jsonBooking = mapper.writeValueAsString(creatingBookingDto);

        mvc.perform(post("/bookings")
                        .header(HEADER_USER_ID, userDtoShort.getId())
                        .content(jsonBooking)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, never()).addBooking(anyLong(), any(CreatingBookingDto.class));
    }

    @Test
    void shouldGetExceptionWithCreateBookingWithoutStart() throws Exception {
        CreatingBookingDto creatingBookingDto = CreatingBookingDto.builder()
                .itemId(1L)
                .end(now.plusSeconds(1))
                .build();
        String jsonBooking = mapper.writeValueAsString(creatingBookingDto);

        mvc.perform(post("/bookings")
                        .header(HEADER_USER_ID, userDtoShort.getId())
                        .content(jsonBooking)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, never()).addBooking(anyLong(), any(CreatingBookingDto.class));
    }

    @Test
    void shouldGetExceptionWithCreateBookingWithoutEnd() throws Exception {
        CreatingBookingDto creatingBookingDto = CreatingBookingDto.builder()
                .itemId(1L)
                .start(now.minusDays(2))
                .build();
        String jsonBooking = mapper.writeValueAsString(creatingBookingDto);

        mvc.perform(post("/bookings")
                        .header(HEADER_USER_ID, userDtoShort.getId())
                        .content(jsonBooking)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, never()).addBooking(anyLong(), any(CreatingBookingDto.class));
    }

    @Test
    void shouldGetExceptionWithCreateBookingWithoutItemId() throws Exception {
        CreatingBookingDto creatingBookingDto = CreatingBookingDto.builder()
                .start(now.minusDays(2))
                .end(now.minusDays(1))
                .build();
        String jsonBooking = mapper.writeValueAsString(creatingBookingDto);

        mvc.perform(post("/bookings")
                        .header(HEADER_USER_ID, userDtoShort.getId())
                        .content(jsonBooking)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, never()).addBooking(anyLong(), any(CreatingBookingDto.class));
    }

    @Test
    void shouldAddBooking() throws Exception {
        when(service.addBooking(anyLong(), any(CreatingBookingDto.class)))
                .thenReturn(bookingDto);

        String jsonBooking = mapper.writeValueAsString(creatingBookingDto);

        mvc.perform(post("/bookings")
                        .header(HEADER_USER_ID, userDtoShort.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE)
                        .content(jsonBooking))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId().intValue())))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().format(DATE_TIME_FORMATTER))))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().format(DATE_TIME_FORMATTER))))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId().intValue())))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId().intValue())))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())));
        verify(service, times(1)).addBooking(userDtoShort.getId(), creatingBookingDto);
    }

    @Test
    void shouldGetExceptionWithApproveBookingWithoutHeader() throws Exception {
        mvc.perform(patch("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, never()).approveBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void shouldGetExceptionWithApproveBookingWithWrongApprove() throws Exception {
        mvc.perform(patch("/bookings/1?approve=kek")
                        .header(HEADER_USER_ID, userDtoShort.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.ALL_VALUE)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, never()).approveBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void shouldApproveBooking() throws Exception {
        when(service.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDto);

        mvc.perform(patch("/bookings/1?approved=true")
                        .header(HEADER_USER_ID, userDtoShort.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId().intValue())))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().format(DATE_TIME_FORMATTER))))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().format(DATE_TIME_FORMATTER))))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId().intValue())))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId().intValue())))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())));
        verify(service, times(1)).approveBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void shouldRejectBooking() throws Exception {
        when(service.approveBooking(anyLong(), anyLong(), anyBoolean()))
                .thenReturn(bookingDto);

        mvc.perform(patch("/bookings/1?approved=false")
                        .header(HEADER_USER_ID, userDtoShort.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId().intValue())))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().format(DATE_TIME_FORMATTER))))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().format(DATE_TIME_FORMATTER))))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId().intValue())))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId().intValue())))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())));
        verify(service, times(1)).approveBooking(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void shouldGetExceptionWithGetBookingByUserOwnerWithoutHeader() throws Exception {
        mvc.perform(get("/bookings/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, never()).getBooking(anyLong(), anyLong());
    }

    @Test
    void shouldGetBookingWithGetBookingByUserOwner() throws Exception {
        when(service.getBooking(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/1")
                        .header(HEADER_USER_ID, userDtoShort.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.ALL_VALUE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId().intValue())))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().format(DATE_TIME_FORMATTER))))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().format(DATE_TIME_FORMATTER))))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())))
                .andExpect(jsonPath("$.booker.id", is(bookingDto.getBooker().getId().intValue())))
                .andExpect(jsonPath("$.item.id", is(bookingDto.getItem().getId().intValue())))
                .andExpect(jsonPath("$.item.name", is(bookingDto.getItem().getName())));
        verify(service, times(1)).getBooking(anyLong(), anyLong());
    }

    @Test
    void shouldGetExceptionWithGetUserBookingsWithoutHeader() throws Exception {
        mvc.perform(get("/bookings")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, never()).getUserBookings(anyLong(), anyString(), any(Pageable.class));
    }

    @Test
    void shouldGetBookingWithGetUserBookings() throws Exception {
        when(service.getUserBookings(anyLong(), anyString(), any(Pageable.class)))
                .thenReturn(bookings);

        mvc.perform(get("/bookings")
                        .header(HEADER_USER_ID, userDtoShort.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[1].id", is(2)));
        verify(service, times(1)).getUserBookings(anyLong(), anyString(), any(Pageable.class));
    }

    @Test
    void shouldGetBookingWithGetUserBookingsWithStateALL() throws Exception {
        when(service.getUserBookings(anyLong(), anyString(), any(Pageable.class)))
                .thenReturn(bookings);

        mvc.perform(get("/bookings?state=ALL")
                        .header(HEADER_USER_ID, userDtoShort.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[1].id", is(2)));
        verify(service, times(1)).getUserBookings(anyLong(), anyString(), any(Pageable.class));
    }

    @Test
    void shouldGetBookingWithGetUserBookingsWithStateCURRENT() throws Exception {
        when(service.getUserBookings(anyLong(), anyString(), any(Pageable.class)))
                .thenReturn(bookings);

        mvc.perform(get("/bookings?state=CURRENT")
                        .header(HEADER_USER_ID, userDtoShort.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[1].id", is(2)));
        verify(service, times(1)).getUserBookings(anyLong(), anyString(), any(Pageable.class));
    }

    @Test
    void shouldGetBookingWithGetUserBookingsWithStatePAST() throws Exception {
        when(service.getUserBookings(anyLong(), anyString(), any(Pageable.class)))
                .thenReturn(bookings);

        mvc.perform(get("/bookings?state=PAST")
                        .header(HEADER_USER_ID, userDtoShort.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[1].id", is(2)));
        verify(service, times(1)).getUserBookings(anyLong(), anyString(), any(Pageable.class));
    }

    @Test
    void shouldGetBookingWithGetUserBookingsWithStateFUTURE() throws Exception {
        when(service.getUserBookings(anyLong(), anyString(), any(Pageable.class)))
                .thenReturn(bookings);

        mvc.perform(get("/bookings?state=FUTURE")
                        .header(HEADER_USER_ID, userDtoShort.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[1].id", is(2)));
        verify(service, times(1)).getUserBookings(anyLong(), anyString(), any(Pageable.class));
    }

    @Test
    void shouldGetBookingWithGetUserBookingsWithStateWAITING() throws Exception {
        when(service.getUserBookings(anyLong(), anyString(), any(Pageable.class)))
                .thenReturn(bookings);

        mvc.perform(get("/bookings?state=WAITING")
                        .header(HEADER_USER_ID, userDtoShort.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[1].id", is(2)));
        verify(service, times(1)).getUserBookings(anyLong(), anyString(), any(Pageable.class));
    }

    @Test
    void shouldGetBookingWithGetUserBookingsWithStateREJECTED() throws Exception {
        when(service.getUserBookings(anyLong(), anyString(), any(Pageable.class)))
                .thenReturn(bookings);

        mvc.perform(get("/bookings?state=REJECTED")
                        .header(HEADER_USER_ID, userDtoShort.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[1].id", is(2)));
        verify(service, times(1)).getUserBookings(anyLong(), anyString(), any(Pageable.class));
    }

    @Test
    void shouldGetExceptionWithGetOwnerBookingsWithoutHeader() throws Exception {
        mvc.perform(get("/bookings/owner")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, never()).getOwnerBookings(anyLong(), anyString(), any(Pageable.class));
    }

    @Test
    void shouldGetBookingWithGetOwnerBookings() throws Exception {
        when(service.getOwnerBookings(anyLong(), anyString(), any(Pageable.class)))
                .thenReturn(bookings);

        mvc.perform(get("/bookings/owner")
                        .header(HEADER_USER_ID, userDtoShort.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[1].id", is(2)));
        verify(service, times(1)).getOwnerBookings(anyLong(), anyString(), any(Pageable.class));
    }

    @Test
    void shouldGetBookingWithGetOwnerBookingsWithStateALL() throws Exception {
        when(service.getOwnerBookings(anyLong(), anyString(), any(Pageable.class)))
                .thenReturn(bookings);

        mvc.perform(get("/bookings/owner?state=ALL")
                        .header(HEADER_USER_ID, userDtoShort.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[1].id", is(2)));
        verify(service, times(1)).getOwnerBookings(anyLong(), anyString(), any(Pageable.class));
    }

    @Test
    void shouldGetBookingWithGetOwnerBookingsWithStateCURRENT() throws Exception {
        when(service.getOwnerBookings(anyLong(), anyString(), any(Pageable.class)))
                .thenReturn(bookings);

        mvc.perform(get("/bookings/owner?state=CURRENT")
                        .header(HEADER_USER_ID, userDtoShort.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[1].id", is(2)));
        verify(service, times(1)).getOwnerBookings(anyLong(), anyString(), any(Pageable.class));
    }

    @Test
    void shouldGetBookingWithGetOwnerBookingsWithStatePAST() throws Exception {
        when(service.getOwnerBookings(anyLong(), anyString(), any(Pageable.class)))
                .thenReturn(bookings);

        mvc.perform(get("/bookings/owner?state=PAST")
                        .header(HEADER_USER_ID, userDtoShort.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[1].id", is(2)));
        verify(service, times(1)).getOwnerBookings(anyLong(), anyString(), any(Pageable.class));
    }

    @Test
    void shouldGetBookingWithGetOwnerBookingsWithStateFUTURE() throws Exception {
        when(service.getOwnerBookings(anyLong(), anyString(), any(Pageable.class)))
                .thenReturn(bookings);

        mvc.perform(get("/bookings/owner?state=FUTURE")
                        .header(HEADER_USER_ID, userDtoShort.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[1].id", is(2)));
        verify(service, times(1)).getOwnerBookings(anyLong(), anyString(), any(Pageable.class));
    }

    @Test
    void shouldGetBookingWithGetOwnerBookingsWithStateWAITING() throws Exception {
        when(service.getOwnerBookings(anyLong(), anyString(), any(Pageable.class)))
                .thenReturn(bookings);

        mvc.perform(get("/bookings/owner?state=WAITING")
                        .header(HEADER_USER_ID, userDtoShort.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[1].id", is(2)));
        verify(service, times(1)).getOwnerBookings(anyLong(), anyString(), any(Pageable.class));
    }

    @Test
    void shouldGetBookingWithGetOwnerBookingsWithStateREJECTED() throws Exception {
        when(service.getOwnerBookings(anyLong(), anyString(), any(Pageable.class)))
                .thenReturn(bookings);

        mvc.perform(get("/bookings/owner?state=REJECTED")
                        .header(HEADER_USER_ID, userDtoShort.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$.[0].id", is(1)))
                .andExpect(jsonPath("$.[1].id", is(2)));
        verify(service, times(1)).getOwnerBookings(anyLong(), anyString(), any(Pageable.class));
    }
}

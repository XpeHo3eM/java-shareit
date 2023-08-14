package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.StatusType;
import ru.practicum.shareit.booking.dal.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreatingBookingDto;
import ru.practicum.shareit.item.dto.item.ItemDtoShort;
import ru.practicum.shareit.user.dto.UserDtoShort;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

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

    @Test
    void shouldGetExceptionWithCreateBookingWithoutHeader() throws Exception {
        mvc.perform(post("/bookings")
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
}

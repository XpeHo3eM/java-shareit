package ru.practicum.shareit.booking.dal;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreatingBookingDto;

import java.util.List;

public interface BookingService {
    BookingDto addBooking(long userId, CreatingBookingDto creatingBookingDto);

    BookingDto approveBooking(long userId, long bookingId, boolean approved);

    BookingDto getBooking(long userId, long bookingId);

    List<BookingDto> getUserBookings(long userId, String state, Pageable pageable);

    List<BookingDto> getOwnerBookings(long userId, String state, Pageable pageable);
}

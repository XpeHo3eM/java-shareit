package ru.practicum.shareit.booking.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.dto.CreatingBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusType;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.user.mapper.UserMapper;

@UtilityClass
public class BookingMapper {
    public BookingDto toDto(Booking booking) {
        return BookingDto.builder()
                .id(booking.getId())
                .start(booking.getDateStart())
                .end(booking.getDateEnd())
                .item(ItemMapper.toDtoShort(booking.getItem()))
                .booker(UserMapper.toDtoShort(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }

    public BookingDtoShort toBookingDtoShort(Booking booking) {
        return BookingDtoShort.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }

    public Booking toBooking(CreatingBookingDto bookingDto) {
        return Booking.builder()
                .dateStart(bookingDto.getStart())
                .dateEnd(bookingDto.getEnd())
                .status(StatusType.WAITING)
                .build();
    }
}

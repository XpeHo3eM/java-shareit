package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoShort;
import ru.practicum.shareit.booking.dto.CreatingBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.dto.item.ItemDtoShort;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDtoShort;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

@Mapper(componentModel = "spring")
public abstract class BookingMapper {
    @Autowired
    protected ItemMapper itemMapper;
    @Autowired
    protected UserMapper userMapper;

    @Mapping(target = "start", source = "booking.dateStart")
    @Mapping(target = "end", source = "booking.dateEnd")
    @Mapping(target = "item", source = "booking.item", qualifiedByName = "itemToItemDtoShort")
    @Mapping(target = "booker", source = "booking.booker", qualifiedByName = "userToUserDtoShort")
    public abstract BookingDto toDto(Booking booking);

    @Mapping(target = "bookerId", source = "booking.booker.id")
    public abstract BookingDtoShort toBookingDtoShort(Booking booking);

    @Mapping(target = "dateStart", source = "bookingDto.start")
    @Mapping(target = "dateEnd", source = "bookingDto.end")
    @Mapping(target = "status", constant = "WAITING")
    public abstract Booking toBooking(CreatingBookingDto bookingDto);

    @Named("itemToItemDtoShort")
    ItemDtoShort itemToItemDtoShort(Item item) {
        return itemMapper.toDtoShort(item);
    }

    @Named("userToUserDtoShort")
    UserDtoShort userToUserDtoShort(User user) {
        return userMapper.toDtoShort(user);
    }
}

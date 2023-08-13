package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dal.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreatingBookingDto;

import java.util.List;

import static ru.practicum.shareit.util.Constant.HEADER_USER_ID;


@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService service;

    @PostMapping
    public BookingDto addBooking(@RequestHeader(HEADER_USER_ID) long userId,
                                 @RequestBody CreatingBookingDto creatingBookingDto) {
        return service.addBooking(userId, creatingBookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@RequestHeader(HEADER_USER_ID) long userId,
                                     @PathVariable long bookingId,
                                     @RequestParam Boolean approved) {
        return service.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@RequestHeader(HEADER_USER_ID) long userId,
                                 @PathVariable long bookingId) {
        return service.getBooking(userId, bookingId);
    }

    @GetMapping
    public List<BookingDto> getBookings(@RequestHeader(HEADER_USER_ID) long userId,
                                        @RequestParam String state,
                                        @RequestParam Integer from,
                                        @RequestParam Integer size) {
        PageRequest page = PageRequest.of(from / size, size, Sort.by("dateStart").descending());

        return service.getUserBookings(userId, state, page);
    }

    @GetMapping("/owner")
    public List<BookingDto> getOwnerBookings(@RequestHeader(HEADER_USER_ID) long userId,
                                             @RequestParam String state,
                                             @RequestParam Integer from,
                                             @RequestParam Integer size) {
        PageRequest page = PageRequest.of(from / size, size, Sort.by("dateStart").descending());

        return service.getOwnerBookings(userId, state, page);
    }
}

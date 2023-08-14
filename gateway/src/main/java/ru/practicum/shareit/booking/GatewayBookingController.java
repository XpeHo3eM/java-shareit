package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.CreatingBookingDto;
import ru.practicum.shareit.util.marker.OnCreate;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.util.Constant.*;

@Controller
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Validated
public class GatewayBookingController {
    private final BookingClient client;

    @PostMapping
    public ResponseEntity<Object> addBooking(@RequestHeader(HEADER_USER_ID) long userId,
                                             @RequestBody @Validated(OnCreate.class) CreatingBookingDto creatingBookingDto) {
        return client.addBooking(userId, creatingBookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader(HEADER_USER_ID) long userId,
                                                 @PathVariable long bookingId,
                                                 @RequestParam Boolean approved) {
        return client.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(HEADER_USER_ID) long userId,
                                             @PathVariable long bookingId) {
        return client.getBooking(userId, bookingId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader(HEADER_USER_ID) long userId,
                                              @RequestParam(defaultValue = "ALL") String state,
                                              @RequestParam(defaultValue = DEFAULT_START_PAGE) @Valid @PositiveOrZero Integer from,
                                              @RequestParam(defaultValue = DEFAULT_SIZE_PAGE) @Valid @Positive Integer size) {
        return client.getBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getOwnerBookings(@RequestHeader(HEADER_USER_ID) long userId,
                                                   @RequestParam(defaultValue = "ALL") String state,
                                                   @RequestParam(defaultValue = DEFAULT_START_PAGE) @Valid @PositiveOrZero Integer from,
                                                   @RequestParam(defaultValue = DEFAULT_SIZE_PAGE) @Valid @Positive Integer size) {
        return client.getOwnerBookings(userId, state, from, size);
    }
}

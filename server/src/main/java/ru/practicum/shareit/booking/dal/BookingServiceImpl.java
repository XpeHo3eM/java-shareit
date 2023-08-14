package ru.practicum.shareit.booking.dal;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.StateType;
import ru.practicum.shareit.booking.StatusType;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreatingBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.NotAvailableBookingException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Validator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingDto addBooking(long userId, CreatingBookingDto creatingBookingDto) {
        User userInRepository = getUserOrThrowException(userId);

        Validator.validateBooking(creatingBookingDto);

        Item itemInRepository = getItemOrThrowException(creatingBookingDto.getItemId());

        if (itemInRepository.getOwner().equals(userInRepository)) {
            throw new EntityNotFoundException(String.format("Пользователь %s является владельцем вещи %s и не может ее забронировать",
                    userInRepository,
                    itemInRepository));
        }

        if (!itemInRepository.getAvailable()) {
            throw new NotAvailableBookingException(String.format("Вещь %s не доступна для бронирования", itemInRepository));
        }

        Booking booking = bookingMapper.toBooking(creatingBookingDto);
        booking.setItem(itemInRepository);
        booking.setBooker(userInRepository);

        return bookingMapper.toDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto approveBooking(long userId, long bookingId, boolean approved) {
        User userInRepository = getUserOrThrowException(userId);
        Booking bookingInRepository = getBookingOrThrowException(bookingId);

        if (bookingInRepository.getItem().getOwner().getId() != userId) {
            throw new AccessDeniedException(String.format("Пользователь %s не является владельцем вещи для подтверждения бронирования", userInRepository));
        }

        StatusType status = approved ? StatusType.APPROVED : StatusType.REJECTED;

        if (approved && bookingInRepository.getStatus() == StatusType.APPROVED) {
            throw new NotAvailableBookingException("Бронирование уже принято");
        }

        bookingInRepository.setStatus(status);

        return bookingMapper.toDto(bookingInRepository);
    }

    @Override
    public BookingDto getBooking(long userId, long bookingId) {
        User userInRepository = getUserOrThrowException(userId);
        Booking bookingInRepository = getBookingOrThrowException(bookingId);

        if (userInRepository != bookingInRepository.getItem().getOwner() && userInRepository != bookingInRepository.getBooker()) {
            throw new AccessDeniedException(String.format("Пользователь %s не является владельцем вещи или автором бронирования", userInRepository));
        }

        return bookingMapper.toDto(bookingInRepository);
    }

    @Override
    public List<BookingDto> getUserBookings(long userId, String state, Pageable pageable) {
        Validator.validateStatusType(state);

        User user = getUserOrThrowException(userId);

        StateType stateType = StateType.valueOf(state.toUpperCase());
        LocalDateTime now = LocalDateTime.now();
        Page<Booking> result = Page.empty();

        switch (stateType) {
            case PAST:
                result = bookingRepository.findAllByBookerAndPast(user, now, pageable);
                break;
            case FUTURE:
                result = bookingRepository.findAllByBookerAndFuture(user, now, pageable);
                break;
            case CURRENT:
                result = bookingRepository.findAllByBookerAndCurrent(user, now, pageable);
                break;
            case WAITING:
                result = bookingRepository.findAllByBookerAndStatus(user, StatusType.WAITING, pageable);
                break;
            case REJECTED:
                result = bookingRepository.findAllByBookerAndStatus(user, StatusType.REJECTED, pageable);
                break;
            case ALL:
                result = bookingRepository.findAllByBooker(user, pageable);
                break;
        }

        return result.stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getOwnerBookings(long userId, String state, Pageable pageable) {
        Validator.validateStatusType(state);

        User user = getUserOrThrowException(userId);

        StateType stateType = StateType.valueOf(state.toUpperCase());
        LocalDateTime now = LocalDateTime.now();
        Page<Booking> result = Page.empty();

        switch (stateType) {
            case PAST:
                result = bookingRepository.findAllByOwnerAndPast(user, now, pageable);
                break;
            case FUTURE:
                result = bookingRepository.findAllByOwnerAndFuture(user, now, pageable);
                break;
            case CURRENT:
                result = bookingRepository.findAllByOwnerAndCurrent(user, now, pageable);
                break;
            case WAITING:
                result = bookingRepository.findAllByOwnerAndStatus(user, StatusType.WAITING, pageable);
                break;
            case REJECTED:
                result = bookingRepository.findAllByOwnerAndStatus(user, StatusType.REJECTED, pageable);
                break;
            case ALL:
                result = bookingRepository.findAllByOwner(user, pageable);
                break;
        }

        return result.stream()
                .map(bookingMapper::toDto)
                .collect(Collectors.toList());
    }

    private User getUserOrThrowException(long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с ID = %d не найден", id)));
    }

    private Item getItemOrThrowException(long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Вещь с ID = %d не найдена", id)));
    }

    private Booking getBookingOrThrowException(long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Бронирование с ID = %d не найдено", id)));
    }
}

package ru.practicum.shareit.booking.dal;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreatingBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StateType;
import ru.practicum.shareit.booking.model.StatusType;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.NotAvailableBookingException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.Validator;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDto addBooking(long userId, CreatingBookingDto creatingBookingDto) {
        User userInRepository = getUserOrThrowException(userId);

        Validator.validateBooking(creatingBookingDto);

        Item itemInRepository = getItemOrThrowException(creatingBookingDto.getItemId());

        if (itemInRepository.getOwner().getId() == userInRepository.getId()) {
            throw new EntityNotFoundException(String.format("Пользователь %s является владельцем вещи %s и не может ее забронировать",
                    userInRepository,
                    itemInRepository));
        }

        if (!itemInRepository.getAvailable()) {
            throw new NotAvailableBookingException(String.format("Вещь %s не доступна для бронирования", itemInRepository));
        }

        Booking booking = BookingMapper.toBooking(creatingBookingDto);
        booking.setItem(itemInRepository);
        booking.setBooker(userInRepository);

        return BookingMapper.toDto(bookingRepository.save(booking));
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

        return BookingMapper.toDto(bookingInRepository);
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getBooking(long userId, long bookingId) {
        User userInRepository = getUserOrThrowException(userId);
        Booking bookingInRepository = getBookingOrThrowException(bookingId);

        if (userInRepository != bookingInRepository.getItem().getOwner() && userInRepository != bookingInRepository.getBooker()) {
            throw new AccessDeniedException(String.format("Пользователь %s не является владельцем вещи или автором бронирования", userInRepository));
        }

        return BookingMapper.toDto(bookingInRepository);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getUserBookings(long userId, String state) {
        User user = getUserOrThrowException(userId);

        Validator.validateStatusType(state);

        StateType stateType = StateType.valueOf(state.toUpperCase());
        LocalDateTime now = LocalDateTime.now();
        List<Booking> result;

        switch (stateType) {
            case PAST:
                result = bookingRepository.findAllByBookerPast(user, now);
                break;
            case FUTURE:
                result = bookingRepository.findAllByBookerFuture(user, now);
                break;
            case CURRENT:
                result = bookingRepository.findAllByBookerCurrent(user, now);
                break;
            case WAITING:
                result = bookingRepository.findAllByBookerAndStatus(user, StatusType.WAITING);
                break;
            case REJECTED:
                result = bookingRepository.findAllByBookerAndStatus(user, StatusType.REJECTED);
                break;
            case ALL:
                result = bookingRepository.findAllByBooker(user);
                break;
            default:
                result = Collections.emptyList();
        }

        return result.stream()
                .map(BookingMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getOwnerBookings(long userId, String state) {
        User user = getUserOrThrowException(userId);

        Validator.validateStatusType(state);

        StateType stateType = StateType.valueOf(state.toUpperCase());
        LocalDateTime now = LocalDateTime.now();
        List<Booking> result;

        switch (stateType) {
            case PAST:
                result = bookingRepository.findAllByOwnerPast(user, now);
                break;
            case FUTURE:
                result = bookingRepository.findAllByOwnerFuture(user, now);
                break;
            case CURRENT:
                result = bookingRepository.findAllByOwnerCurrent(user, now);
                break;
            case WAITING:
                result = bookingRepository.findAllByOwnerAndStatus(user, StatusType.WAITING);
                break;
            case REJECTED:
                result = bookingRepository.findAllByOwnerAndStatus(user, StatusType.REJECTED);
                break;
            case ALL:
                result = bookingRepository.findAllByOwner(user);
                break;
            default:
                result = Collections.emptyList();
        }

        return result.stream()
                .map(BookingMapper::toDto)
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

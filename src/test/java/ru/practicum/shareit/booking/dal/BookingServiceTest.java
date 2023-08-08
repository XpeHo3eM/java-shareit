package ru.practicum.shareit.booking.dal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.*;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.CreatingBookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusType;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.NotAvailableBookingException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.item.ItemDtoShort;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dto.UserDtoShort;
import ru.practicum.shareit.user.model.User;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

class BookingServiceTest {
    private static UserRepository userRepository;
    private static ItemRepository itemRepository;
    private static BookingRepository bookingRepository;
    private static BookingService bookingService;
    private static BookingMapper bookingMapper;

    private final User user = User.builder()
            .id(1L)
            .name("user")
            .email("email@ya.ru")
            .build();
    private final User user2 = User.builder()
            .id(2L)
            .name("user2")
            .email("email2@ya.ru")
            .build();
    private final Item item = Item.builder()
            .id(1L)
            .name("item")
            .description("description")
            .available(true)
            .owner(user)
            .build();
    private final Item itemNotAvailable = Item.builder()
            .id(2L)
            .name("item2")
            .description("description2")
            .available(false)
            .owner(user)
            .build();
    private final LocalDateTime start = LocalDateTime.now().minusDays(1);
    private final LocalDateTime end = LocalDateTime.now().plusDays(1);
    private final UserDtoShort booker = UserDtoShort.builder()
            .id(2L)
            .build();
    private final CreatingBookingDto creatingBookingDto = CreatingBookingDto.builder()
            .itemId(item.getId())
            .start(start)
            .end(end)
            .build();
    private final Booking booking = Booking.builder()
            .id(1L)
            .dateStart(start)
            .dateEnd(end)
            .item(item)
            .build();
    private final CreatingBookingDto creatingBookingDtoWithNotAvailableItem = CreatingBookingDto.builder()
            .itemId(itemNotAvailable.getId())
            .start(start)
            .end(end)
            .build();
    private final Booking bookingApproved = Booking.builder()
            .id(1L)
            .status(StatusType.APPROVED)
            .booker(user2)
            .dateStart(start)
            .dateEnd(end)
            .item(item)
            .build();
    private final Booking bookingRejected = Booking.builder()
            .id(2L)
            .status(StatusType.REJECTED)
            .booker(user2)
            .dateStart(start)
            .dateEnd(end)
            .item(item)
            .build();
    private final Booking bookingWaiting = Booking.builder()
            .id(3L)
            .status(StatusType.WAITING)
            .booker(user2)
            .dateStart(start)
            .dateEnd(end)
            .item(item)
            .build();
    private final ItemDtoShort itemDtoShort = ItemDtoShort.builder()
            .id(1L)
            .name("item")
            .build();
    private final BookingDto bookingApprovedDto = BookingDto.builder()
            .id(1L)
            .status(StatusType.APPROVED)
            .booker(booker)
            .start(start)
            .end(end)
            .item(itemDtoShort)
            .build();
    private final BookingDto bookingRejectedDto = BookingDto.builder()
            .id(2L)
            .status(StatusType.REJECTED)
            .booker(booker)
            .start(start)
            .end(end)
            .item(itemDtoShort)
            .build();
    private final BookingDto bookingWaitingDto = BookingDto.builder()
            .id(3L)
            .status(StatusType.WAITING)
            .booker(booker)
            .start(start)
            .end(end)
            .item(itemDtoShort)
            .build();
    private final List<Booking> bookings = List.of(bookingRejected, bookingApproved, bookingWaiting);
    private final Page<Booking> bookingsPage = listBookingToPage();
    private final PageRequest page = PageRequest.of(0, 5, Sort.by("dateStart").descending());


    @BeforeEach
    void initialize() {
        bookingRepository = Mockito.mock(BookingRepository.class);
        itemRepository = Mockito.mock(ItemRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        bookingMapper = Mockito.mock(BookingMapper.class);
        bookingService = new BookingServiceImpl(bookingRepository, userRepository, itemRepository, bookingMapper);
    }

    @Test
    void shouldCreateBooking() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user2));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(bookingWaiting);
        when(bookingMapper.toBooking(any(CreatingBookingDto.class)))
                .thenReturn(booking);
        when(bookingMapper.toDto(any(Booking.class)))
                .thenReturn(bookingWaitingDto);

        BookingDto getBookingDto = bookingService.addBooking(2L, creatingBookingDto);

        assertThat(getBookingDto)
                .hasFieldOrPropertyWithValue("id", 3L)
                .hasFieldOrPropertyWithValue("start", start)
                .hasFieldOrPropertyWithValue("end", end)
                .hasFieldOrPropertyWithValue("status", StatusType.WAITING)
                .hasFieldOrPropertyWithValue("booker", booker)
                .hasFieldOrPropertyWithValue("item", itemDtoShort);
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void shouldGetExceptionCreateBookingNotAvailable() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user2));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(itemNotAvailable));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(bookingApproved);

        assertThrows(NotAvailableBookingException.class, () -> bookingService.addBooking(2L, creatingBookingDtoWithNotAvailableItem));
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldGetExceptionCreateBookingNotFoundUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(bookingApproved);

        assertThrows(EntityNotFoundException.class, () -> bookingService.addBooking(2L, creatingBookingDto));
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, never()).findById(anyLong());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void shouldGetExceptionCreateBookingNotFoundItem() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(bookingApproved);

        assertThrows(EntityNotFoundException.class, () -> bookingService.addBooking(2L, creatingBookingDto));
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void shouldGetExceptionCreateBookingNotValidDateException() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(bookingApproved);

        CreatingBookingDto creatingBookingDtoIncorrect = CreatingBookingDto.builder()
                .itemId(item.getId())
                .start(end)
                .end(start)
                .build();
        assertThrows(ValidationException.class, () -> bookingService.addBooking(2L, creatingBookingDtoIncorrect));
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, never()).findById(anyLong());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void shouldGetExceptionCreateBookingNotFoundSelfItem() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(bookingApproved);

        assertThrows(EntityNotFoundException.class, () -> bookingService.addBooking(1L, creatingBookingDto));
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void shouldApproveBooking() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(bookingWaiting));
        when(bookingMapper.toDto(any(Booking.class)))
                .thenReturn(bookingApprovedDto);

        BookingDto getBookingDto = bookingService.approveBooking(1L, 3L, true);

        assertThat(getBookingDto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("start", start)
                .hasFieldOrPropertyWithValue("end", end)
                .hasFieldOrPropertyWithValue("status", StatusType.APPROVED)
                .hasFieldOrPropertyWithValue("booker", booker)
                .hasFieldOrPropertyWithValue("item", itemDtoShort);
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldRejectBooking() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user2));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(bookingRejected));
        when(bookingMapper.toDto(any(Booking.class)))
                .thenReturn(bookingRejectedDto);
        BookingDto getBookingDto = bookingService.approveBooking(1L, 1L, false);

        assertThat(getBookingDto)
                .hasFieldOrPropertyWithValue("id", 2L)
                .hasFieldOrPropertyWithValue("start", start)
                .hasFieldOrPropertyWithValue("end", end)
                .hasFieldOrPropertyWithValue("status", StatusType.REJECTED)
                .hasFieldOrPropertyWithValue("booker", booker)
                .hasFieldOrPropertyWithValue("item", itemDtoShort);
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldGetExceptionWithApproveBookingNoFoundUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(bookingApproved));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(bookingApproved);

        assertThrows(EntityNotFoundException.class, () -> bookingService.approveBooking(1L, 1L, true));
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, never()).save(any(Booking.class));
        verify(bookingRepository, never()).findById(anyLong());
    }

    @Test
    void shouldGetExceptionWithApproveBookingNoFoundBooking() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(bookingApproved);

        assertThrows(EntityNotFoundException.class, () -> bookingService.approveBooking(1L, 1L, true));
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, never()).save(any(Booking.class));
        verify(bookingRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldGetExceptionWithApproveBookingNoFoundOwner() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(bookingApproved));
        when(bookingRepository.save(any(Booking.class)))
                .thenReturn(bookingApproved);

        assertThrows(AccessDeniedException.class, () -> bookingService.approveBooking(2L, 1L, true));
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, never()).save(any(Booking.class));
        verify(bookingRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldGetExceptionWithApproveBookingNotAvailableAlreadyApproved() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(bookingApproved));

        assertThrows(NotAvailableBookingException.class, () -> bookingService.approveBooking(1L, 1L, true));
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, never()).save(any(Booking.class));
        verify(bookingRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldGetBookingByUserOwnerItem() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(bookingWaiting));
        when(bookingMapper.toDto(any(Booking.class)))
                .thenReturn(bookingWaitingDto);

        BookingDto getBookingDto = bookingService.getBooking(1L, 3L);

        assertThat(getBookingDto)
                .hasFieldOrPropertyWithValue("id", 3L)
                .hasFieldOrPropertyWithValue("start", start)
                .hasFieldOrPropertyWithValue("end", end)
                .hasFieldOrPropertyWithValue("status", StatusType.WAITING)
                .hasFieldOrPropertyWithValue("booker", booker)
                .hasFieldOrPropertyWithValue("item", itemDtoShort);
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldGetBookingByUserOwnerBooking() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(bookingApproved));
        when(bookingMapper.toDto(any(Booking.class)))
                .thenReturn(bookingApprovedDto);

        BookingDto getBookingDto = bookingService.getBooking(2L, 1L);

        assertThat(getBookingDto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("start", start)
                .hasFieldOrPropertyWithValue("end", end)
                .hasFieldOrPropertyWithValue("status", StatusType.APPROVED)
                .hasFieldOrPropertyWithValue("booker", booker)
                .hasFieldOrPropertyWithValue("item", itemDtoShort);
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldGetExceptionWithGetBookingByUserOwnerNotFoundUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(bookingApproved));

        assertThrows(EntityNotFoundException.class, () -> bookingService.getBooking(1L, 1L));
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, never()).findById(anyLong());
    }

    @Test
    void shouldGetExceptionWithGetBookingByUserOwnerNotFoundBooking() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> bookingService.getBooking(1L, 1L));
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldGetExceptionWithGetBookingByUserOwnerNotFoundOwner() {
        final User user3 = User.builder()
                .id(3L)
                .name("user3")
                .email("email3@ya.ru")
                .build();

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user3));
        when(bookingRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(bookingApproved));

        assertThrows(AccessDeniedException.class, () -> bookingService.getBooking(666L, 1L));
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldGetUserBookingsWithAll() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findAllByBooker(any(User.class), any(Pageable.class)))
                .thenReturn(bookingsPage);

        List<BookingDto> bookings = bookingService.getUserBookings(1L, "aLl", page);

        assertThat(bookings).asList()
                .hasSize(3);
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findAllByBooker(any(User.class), any(Pageable.class));
    }

    @Test
    void shouldGetExceptionWithGetUserBookingsWithAll() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(bookingRepository.findAllByBooker(any(User.class), any(Pageable.class)))
                .thenReturn(bookingsPage);

        assertThrows(EntityNotFoundException.class, () -> bookingService.getUserBookings(1L, "aLl", page));
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, never()).findAllByBooker(any(User.class), any(Pageable.class));
    }

    @Test
    void shouldGetUserBookingsWithCurrent() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findAllByBookerAndCurrent(any(User.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(bookingsPage);

        List<BookingDto> bookings = bookingService.getUserBookings(1L, "cuRRenT", page);

        assertThat(bookings).asList()
                .hasSize(3);
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findAllByBookerAndCurrent(any(User.class), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void shouldGetUserBookingsWithPast() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findAllByBookerAndPast(any(User.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(bookingsPage);

        List<BookingDto> bookings = bookingService.getUserBookings(1L, "pAST", page);

        assertThat(bookings).asList()
                .hasSize(3);
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findAllByBookerAndPast(any(User.class), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void shouldGetUserBookingsWithFuture() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findAllByBookerAndFuture(any(User.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(bookingsPage);

        List<BookingDto> bookings = bookingService.getUserBookings(1L, "FUTURE", page);

        assertThat(bookings).asList()
                .hasSize(3);
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findAllByBookerAndFuture(any(User.class), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void shouldGetUserBookingsWithWaiting() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findAllByBookerAndStatus(any(User.class), any(StatusType.class), any(Pageable.class)))
                .thenReturn(bookingsPage);

        List<BookingDto> bookings = bookingService.getUserBookings(1L, "WAITING", page);

        assertThat(bookings).asList()
                .hasSize(3);
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findAllByBookerAndStatus(any(User.class), any(StatusType.class), any(Pageable.class));
    }

    @Test
    void shouldGetUserBookingsWithReject() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findAllByBookerAndStatus(any(User.class), any(StatusType.class), any(Pageable.class)))
                .thenReturn(bookingsPage);

        List<BookingDto> bookings = bookingService.getUserBookings(1L, "rejected", page);

        assertThat(bookings).asList()
                .hasSize(3);
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findAllByBookerAndStatus(any(User.class), any(StatusType.class), any(Pageable.class));
    }

    @Test
    void shouldGetOwnerBookingsWithAll() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findAllByOwner(any(User.class), any(Pageable.class)))
                .thenReturn(bookingsPage);

        List<BookingDto> bookings = bookingService.getOwnerBookings(1L, "aLl", page);

        assertThat(bookings).asList()
                .hasSize(3);
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findAllByOwner(any(User.class), any(Pageable.class));
    }

    @Test
    void shouldGetExceptionWithOwnerBookingsWithAll() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(bookingRepository.findAllByBooker(any(User.class), any(Pageable.class)))
                .thenReturn(bookingsPage);

        assertThrows(EntityNotFoundException.class, () -> bookingService.getOwnerBookings(1L, "aLl", page));
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, never()).findAllByBooker(any(User.class), any(Pageable.class));
    }

    @Test
    void shouldGetOwnerBookingsWithCurrent() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findAllByOwnerAndCurrent(any(User.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(bookingsPage);

        List<BookingDto> bookings = bookingService.getOwnerBookings(1L, "cuRRenT", page);

        assertThat(bookings).asList()
                .hasSize(3);
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findAllByOwnerAndCurrent(any(User.class), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void shouldGetOwnerBookingsWithPast() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findAllByOwnerAndPast(any(User.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(bookingsPage);

        List<BookingDto> bookings = bookingService.getOwnerBookings(1L, "pAST", page);

        assertThat(bookings).asList()
                .hasSize(3);
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findAllByOwnerAndPast(any(User.class), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void shouldGetOwnerBookingsWithFuture() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findAllByOwnerAndFuture(any(User.class), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(bookingsPage);

        List<BookingDto> bookings = bookingService.getOwnerBookings(1L, "FUTURE", page);

        assertThat(bookings).asList()
                .hasSize(3);
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findAllByOwnerAndFuture(any(User.class), any(LocalDateTime.class), any(Pageable.class));
    }

    @Test
    void shouldGetOwnerBookingsWithWaiting() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findAllByOwnerAndStatus(any(User.class), any(StatusType.class), any(Pageable.class)))
                .thenReturn(bookingsPage);

        List<BookingDto> bookings = bookingService.getOwnerBookings(1L, "WAITING", page);

        assertThat(bookings).asList()
                .hasSize(3);
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findAllByOwnerAndStatus(any(User.class), any(StatusType.class), any(Pageable.class));
    }

    @Test
    void shouldGetOwnerBookingsWithReject() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(user));
        when(bookingRepository.findAllByOwnerAndStatus(any(User.class), any(StatusType.class), any(Pageable.class)))
                .thenReturn(bookingsPage);

        List<BookingDto> bookings = bookingService.getOwnerBookings(1L, "rejected", page);

        assertThat(bookings).asList()
                .hasSize(3);
        verify(userRepository, times(1)).findById(anyLong());
        verify(bookingRepository, times(1)).findAllByOwnerAndStatus(any(User.class), any(StatusType.class), any(Pageable.class));
    }

    private Page<Booking> listBookingToPage() {
        return new PageImpl<>(bookings.subList(0, 3),
                PageRequest.of(0, 3),
                bookings.size());
    }
}
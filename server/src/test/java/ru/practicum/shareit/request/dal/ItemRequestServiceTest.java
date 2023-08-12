package ru.practicum.shareit.request.dal;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.*;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.CreatingItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
class ItemRequestServiceTest {
    @Autowired
    ItemRequestMapper itemRequestMapper;
    private static UserRepository userRepository;
    public static ItemRepository itemRepository;
    private static ItemRequestService requestService;
    private static ItemRequestRepository requestRepository;
    private final LocalDateTime now = LocalDateTime.now();
    private final User user = User.builder()
            .id(1L)
            .name("name")
            .email("email@ya.ru")
            .build();
    private final ItemRequest itemRequest = ItemRequest.builder()
            .id(1L)
            .description("description")
            .requester(user)
            .created(now)
            .build();
    private final ItemRequest itemRequest2 = ItemRequest.builder()
            .id(2L)
            .description("description2")
            .requester(user)
            .created(now)
            .build();
    private final CreatingItemRequestDto requestDto = CreatingItemRequestDto.builder()
            .description("description")
            .build();
    private final List<ItemRequest> requests = List.of(itemRequest, itemRequest2);
    private final PageRequest page = PageRequest.of(0, 7, Sort.by("created").descending());

    @BeforeEach
    void initialize() {
        userRepository = Mockito.mock(UserRepository.class);
        itemRepository = Mockito.mock(ItemRepository.class);
        requestRepository = Mockito.mock(ItemRequestRepository.class);
        requestService = new ItemRequestServiceImpl(userRepository, itemRepository, requestRepository, itemRequestMapper);
    }

    @Test
    void shouldCreateRequest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(requestRepository.save(any(ItemRequest.class)))
                .thenReturn(itemRequest);

        ItemRequestDto getItemRequestDto = requestService.addItemRequest(user.getId(), requestDto);

        assertThat(getItemRequestDto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("description", requestDto.getDescription())
                .hasFieldOrProperty("created");
        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, times(1)).save(any(ItemRequest.class));
    }

    @Test
    void shouldGetExceptionWithCreateRequestNotFoundUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(requestRepository.save(any(ItemRequest.class)))
                .thenReturn(itemRequest);

        assertThrows(EntityNotFoundException.class, () -> requestService.addItemRequest(user.getId(), requestDto));
        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, never()).save(any(ItemRequest.class));
    }

    @Test
    void shouldGetRequestById() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(itemRequest));

        ItemRequestDto getItemRequestDto = requestService.getItemRequest(user.getId(), itemRequest.getId());

        assertThat(getItemRequestDto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("description", requestDto.getDescription())
                .hasFieldOrProperty("created")
                .hasFieldOrPropertyWithValue("items", new ArrayList<>());
        verify(userRepository, times(1)).existsById(anyLong());
        verify(requestRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldGetExceptionWithRequestByIdNotFoundUser() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);
        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(itemRequest));

        assertThrows(EntityNotFoundException.class, () -> requestService.getItemRequest(user.getId(), itemRequest.getId()));
        verify(userRepository, times(1)).existsById(anyLong());
        verify(requestRepository, never()).findById(anyLong());
    }

    @Test
    void shouldGetExceptionWithRequestByIdNotFoundRequest() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(requestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> requestService.getItemRequest(user.getId(), itemRequest.getId()));
        verify(userRepository, times(1)).existsById(anyLong());
        verify(requestRepository, times(1)).findById(anyLong());
    }

    @Test
    void shouldGetAllRequestsByUserId() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(requestRepository.findAllByRequester(any(User.class)))
                .thenReturn(requests);

        List<ItemRequestDto> requests = requestService.getOwnerItemRequests(user.getId());

        assertThat(requests).asList()
                .hasSize(2)
                .satisfies(list -> assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 1L)
                        .hasFieldOrPropertyWithValue("description", requestDto.getDescription())
                        .hasFieldOrProperty("created")
                        .hasFieldOrPropertyWithValue("items", new ArrayList<>()));
        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, times(1)).findAllByRequester(any(User.class));
    }

    @Test
    void shouldGetExceptionWithGetAllRequestsByUserIdNotFoundUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(requestRepository.findAllByRequester(any(User.class)))
                .thenReturn(requests);

        assertThrows(EntityNotFoundException.class, () -> requestService.getOwnerItemRequests(user.getId()));
        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, never()).findAllByRequester(any(User.class));
    }

    @Test
    void shouldGetAllRequests() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(requestRepository.findAllByRequesterNot(any(User.class), any(Pageable.class)))
                .thenReturn(listItemRequestToPage());

        List<ItemRequestDto> requests = requestService.getAllItemRequests(user.getId(), page);

        assertThat(requests)
                .hasSize(2)
                .satisfies(list -> assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 1L)
                        .hasFieldOrPropertyWithValue("description", requestDto.getDescription())
                        .hasFieldOrProperty("created")
                        .hasFieldOrPropertyWithValue("items", new ArrayList<>()));
        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, times(1)).findAllByRequesterNot(any(User.class), any(Pageable.class));
    }

    @Test
    void shouldGetExceptionWithGetAllRequestsNotFoundUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(requestRepository.findAllByRequesterNot(any(User.class), any(Pageable.class)))
                .thenReturn(listItemRequestToPage());

        assertThrows(EntityNotFoundException.class, () -> requestService.getAllItemRequests(user.getId(), page));
        verify(userRepository, times(1)).findById(anyLong());
        verify(requestRepository, never()).findAllByRequesterNot(any(User.class), any(Pageable.class));
    }

    private Page<ItemRequest> listItemRequestToPage() {
        return new PageImpl<>(requests.subList(0, 2),
                PageRequest.of(0, 2),
                requests.size());
    }
}

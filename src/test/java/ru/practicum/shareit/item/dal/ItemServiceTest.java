package ru.practicum.shareit.item.dal;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.StatusType;
import ru.practicum.shareit.exception.AccessDeniedException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.exception.NotAvailableBookingException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.comment.CreatingCommentDto;
import ru.practicum.shareit.item.dto.item.CreatingItemDto;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemServiceTest {
    private static ItemRepository itemRepository;
    private static UserRepository userRepository;
    private static CommentRepository commentRepository;
    private static ItemRequestRepository itemRequestRepository;
    private static ItemService itemService;

    private final LocalDateTime now = LocalDateTime.now();
    private final User user = User.builder()
            .id(1L)
            .name("user1")
            .email("user1@email.com")
            .build();
    private final User user2 = User.builder()
            .id(2L)
            .name("user2")
            .email("user2@email.com")
            .build();
    private final ItemRequest request = ItemRequest.builder()
            .id(1L)
            .description("request")
            .requester(user)
            .created(now)
            .build();
    private final CreatingItemDto creatingItemDto = CreatingItemDto.builder()
            .name("item")
            .description("description")
            .available(true)
            .requestId(1L)
            .build();
    private final Item item = Item.builder()
            .id(1L)
            .name("item")
            .description("description")
            .available(true)
            .owner(user)
            .itemRequest(request)
            .bookings(Collections.emptySet())
            .comments(Collections.emptySet())
            .build();
    private final Item item2 = Item.builder()
            .id(2L)
            .name("item2")
            .description("description2")
            .available(false)
            .owner(user)
            .bookings(Collections.emptySet())
            .comments(Collections.emptySet())
            .build();
    private final Item updatedItem = Item.builder()
            .id(1L)
            .name("updated")
            .description("updated")
            .available(true)
            .owner(user)
            .itemRequest(request)
            .bookings(Collections.emptySet())
            .comments(Collections.emptySet())
            .build();
    private final List<Item> items = List.of(item, item2);
    private final CreatingItemDto updatedItemDto = CreatingItemDto.builder()
            .name("updated")
            .description("updated")
            .available(true)
            .build();
    private final CreatingCommentDto creatingCommentDto = CreatingCommentDto.builder()
            .text("comment")
            .build();
    private final Comment comment = Comment.builder()
            .id(1L)
            .text("comment")
            .author(user)
            .creationDate(LocalDateTime.now())
            .build();
    private final Booking booking = Booking.builder()
            .id(1L)
            .dateStart(now.minusSeconds(666))
            .dateEnd(now.minusSeconds(13))
            .item(item)
            .booker(user2)
            .status(StatusType.APPROVED)
            .build();


    @BeforeEach
    void initialize() {
        itemRepository = Mockito.mock(ItemRepository.class);
        userRepository = Mockito.mock(UserRepository.class);
        commentRepository = Mockito.mock(CommentRepository.class);
        itemRequestRepository = Mockito.mock(ItemRequestRepository.class);
        itemService = new ItemServiceImpl(itemRepository, userRepository, commentRepository, itemRequestRepository);
    }

    @Test
    void shouldCreateItemWithRequest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(request));
        when(commentRepository.findById(anyLong()))
                .thenReturn(null);
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        ItemDto itemDto = itemService.addItem(user.getId(), creatingItemDto);

        assertThat(itemDto)
                .hasFieldOrPropertyWithValue("id", 1L)
                .hasFieldOrPropertyWithValue("name", creatingItemDto.getName())
                .hasFieldOrPropertyWithValue("description", creatingItemDto.getDescription())
                .hasFieldOrPropertyWithValue("requestId", 1L)
                .hasFieldOrPropertyWithValue("lastBooking", null)
                .hasFieldOrPropertyWithValue("nextBooking", null)
                .hasFieldOrPropertyWithValue("comments", Collections.emptyList());
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, times(1)).findById(anyLong());
        verify(commentRepository, never()).findById(anyLong());
        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void shouldGetExceptionWithCreateWithNotFoundUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(request));
        when(commentRepository.findById(anyLong()))
                .thenReturn(null);
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        assertThrows(EntityNotFoundException.class, () -> itemService.addItem(1L, creatingItemDto));

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, never()).findById(anyLong());
        verify(commentRepository, never()).findById(anyLong());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void shouldGetExceptionWithCreateWithNotFoundRequest() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(commentRepository.findById(anyLong()))
                .thenReturn(null);
        when(itemRepository.save(any(Item.class)))
                .thenReturn(item);

        assertThrows(EntityNotFoundException.class, () -> itemService.addItem(1L, creatingItemDto));

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, times(1)).findById(anyLong());
        verify(commentRepository, never()).findById(anyLong());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void shouldGetExceptionWithUpdateItemWithNotFoundUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(request));
        when(commentRepository.findById(anyLong()))
                .thenReturn(null);
        when(itemRepository.findByIdWithOwner(anyLong()))
                .thenReturn(Optional.of(item));
        when(itemRepository.save(updatedItem))
                .thenReturn(updatedItem);

        assertThrows(EntityNotFoundException.class, () -> itemService.updateItem(1L, 1L, updatedItemDto));
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, never()).findById(anyLong());
        verify(commentRepository, never()).findById(anyLong());
        verify(itemRepository, never()).findByIdWithOwner(anyLong());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void shouldGetExceptionWithUpdateItemWithNotFoundItem() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(request));
        when(commentRepository.findById(anyLong()))
                .thenReturn(null);
        when(itemRepository.findByIdWithOwner(anyLong()))
                .thenReturn(Optional.empty());
        when(itemRepository.save(updatedItem))
                .thenReturn(updatedItem);

        assertThrows(EntityNotFoundException.class, () -> itemService.updateItem(1L, 1L, updatedItemDto));
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, never()).findById(anyLong());
        verify(commentRepository, never()).findById(anyLong());
        verify(itemRepository, times(1)).findByIdWithOwner(anyLong());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void shouldGetExceptionWithUpdateItemWithNotFoundOwner() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user.toBuilder().id(2L).build()));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(request));
        when(commentRepository.findById(anyLong()))
                .thenReturn(null);
        when(itemRepository.findByIdWithOwner(anyLong()))
                .thenReturn(Optional.of(item));
        when(itemRepository.save(updatedItem))
                .thenReturn(updatedItem);

        assertThrows(AccessDeniedException.class, () -> itemService.updateItem(1L, 1L, updatedItemDto));
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, never()).findById(anyLong());
        verify(commentRepository, never()).findById(anyLong());
        verify(itemRepository, times(1)).findByIdWithOwner(anyLong());
        verify(itemRepository, never()).save(any(Item.class));
    }

    @Test
    void shouldUpdateItem() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(request));
        when(commentRepository.findById(anyLong()))
                .thenReturn(null);
        when(itemRepository.findByIdWithOwner(anyLong()))
                .thenReturn(Optional.of(updatedItem));

        ItemDto itemDto = itemService.updateItem(user.getId(), updatedItem.getId(), updatedItemDto);

        assertThat(itemDto)
                .hasFieldOrPropertyWithValue("id", updatedItem.getId())
                .hasFieldOrPropertyWithValue("name", updatedItemDto.getName())
                .hasFieldOrPropertyWithValue("description", updatedItemDto.getDescription())
                .hasFieldOrPropertyWithValue("requestId", 1L)
                .hasFieldOrPropertyWithValue("lastBooking", null)
                .hasFieldOrPropertyWithValue("nextBooking", null)
                .hasFieldOrPropertyWithValue("comments", Collections.emptyList());
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, never()).findById(anyLong());
        verify(commentRepository, never()).findById(anyLong());
        verify(itemRepository, times(1)).findByIdWithOwner(anyLong());
    }

    @Test
    void shouldDeleteItem() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(request));
        when(commentRepository.findById(anyLong()))
                .thenReturn(null);
        when(itemRepository.findByIdWithOwner(anyLong()))
                .thenReturn(Optional.of(item));
        doNothing().when(itemRepository).deleteById(anyLong());

        itemService.deleteItem(user.getId(), item.getId());

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, never()).findById(anyLong());
        verify(commentRepository, never()).findById(anyLong());
        verify(itemRepository, times(1)).findByIdWithOwner(anyLong());
        verify(itemRepository, times(1)).deleteById(anyLong());
    }

    @Test
    void shouldGetExceptionWithDeleteItemWithNotFoundUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(request));
        when(commentRepository.findById(anyLong()))
                .thenReturn(null);
        when(itemRepository.findByIdWithOwner(anyLong()))
                .thenReturn(Optional.of(item));
        doNothing().when(itemRepository).deleteById(anyLong());

        assertThrows(EntityNotFoundException.class, () -> itemService.updateItem(1L, 1L, updatedItemDto));
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, never()).findById(anyLong());
        verify(commentRepository, never()).findById(anyLong());
        verify(itemRepository, never()).findById(anyLong());
        verify(itemRepository, never()).deleteById(anyLong());
    }

    @Test
    void shouldGetExceptionWithDeleteItemWithNotFoundItem() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(request));
        when(commentRepository.findById(anyLong()))
                .thenReturn(null);
        when(itemRepository.findByIdWithOwner(anyLong()))
                .thenReturn(Optional.empty());
        doNothing().when(itemRepository).deleteById(anyLong());

        assertThrows(EntityNotFoundException.class, () -> itemService.deleteItem(1L, 1L));
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, never()).findById(anyLong());
        verify(commentRepository, never()).findById(anyLong());
        verify(itemRepository, times(1)).findByIdWithOwner(anyLong());
        verify(itemRepository, never()).deleteById(anyLong());
    }

    @Test
    void shouldGetExceptionWithDeleteItemWithNotFoundOwner() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user.toBuilder().id(2L).build()));
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(request));
        when(commentRepository.findById(anyLong()))
                .thenReturn(null);
        when(itemRepository.findByIdWithOwner(anyLong()))
                .thenReturn(Optional.of(item));
        doNothing().when(itemRepository).deleteById(anyLong());

        assertThrows(AccessDeniedException.class, () -> itemService.deleteItem(1L, 1L));
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, never()).findById(anyLong());
        verify(commentRepository, never()).findById(anyLong());
        verify(itemRepository, times(1)).findByIdWithOwner(anyLong());
        verify(itemRepository, never()).deleteById(anyLong());
    }

    @Test
    void shouldGetByIdByOwner() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findByIdWithOwner(anyLong()))
                .thenReturn(Optional.of(item));

        itemService.getItemById(user.getId(), item.getId());

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, never()).findById(anyLong());
        verify(commentRepository, never()).findById(anyLong());
        verify(itemRepository, times(1)).findByIdWithOwner(anyLong());
    }

    @Test
    void shouldGetByIdByNotOwner() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user.toBuilder().id(2L).build()));
        when(itemRepository.findByIdWithOwner(anyLong()))
                .thenReturn(Optional.of(item));

        itemService.getItemById(2L, item.getId());

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, never()).findById(anyLong());
        verify(commentRepository, never()).findById(anyLong());
        verify(itemRepository, times(1)).findByIdWithOwner(anyLong());
    }

    @Test
    void shouldGetExceptionGetByIdByWithNotFoundUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(itemRepository.findByIdWithOwner(anyLong()))
                .thenReturn(Optional.of(item));

        assertThrows(EntityNotFoundException.class, () -> itemService.getItemById(1L, 1L));
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, never()).findById(anyLong());
        verify(commentRepository, never()).findById(anyLong());
        verify(itemRepository, never()).findById(anyLong());
    }

    @Test
    void shouldGetExceptionGetByIdByWithNotFoundItem() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findByIdWithOwner(anyLong()))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.getItemById(1L, 1L));
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, never()).findById(anyLong());
        verify(commentRepository, never()).findById(anyLong());
        verify(itemRepository, times(1)).findByIdWithOwner(anyLong());
    }

    @Test
    void shouldGetAllByUserIdByOwner() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRepository.findAllByOwnerId(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(items));

        List<ItemDto> items = itemService.getAllItemsByUserId(1L, 3, 10);

        assertThat(items).asList()
                .isNotEmpty()
                .hasSize(2)
                .satisfies(list -> {
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("name", "item");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("description", "description");
                });
        verify(itemRequestRepository, never()).findById(anyLong());
        verify(commentRepository, never()).findById(anyLong());
        verify(itemRepository, times(1)).findAllByOwnerId(anyLong(), any(Pageable.class));
    }

    @Test
    void shouldGetAllByUserIdByNotOwner() {
        when(itemRepository.findAllByOwnerId(anyLong(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(items));
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);

        List<ItemDto> items = itemService.getAllItemsByUserId(2L, 7, 3);

        assertThat(items).asList()
                .isEmpty();
        verify(itemRequestRepository, never()).findById(anyLong());
        verify(commentRepository, never()).findById(anyLong());
        verify(itemRepository, times(1)).findAllByOwnerId(anyLong(), any(Pageable.class));
    }

    @Test
    void shouldSearch() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRepository.search(anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(items));

        List<ItemDto> items = itemService.search(1L, "text", 7, 3);

        assertThat(items).asList()
                .hasSize(2)
                .satisfies(list -> {
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("name", "item");
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("description", "description");
                });
        verify(userRepository, times(1)).existsById(anyLong());
        verify(itemRequestRepository, never()).findById(anyLong());
        verify(commentRepository, never()).findById(anyLong());
        verify(itemRepository, times(1)).search(anyString(), any(Pageable.class));
    }

    @Test
    void shouldExceptionWithSearchNotFoundUser() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(false);
        when(itemRepository.search(anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(items));

        assertThrows(EntityNotFoundException.class, () -> itemService.search(1L, "text", 7, 3));
        verify(userRepository, times(1)).existsById(anyLong());
        verify(itemRequestRepository, never()).findById(anyLong());
        verify(commentRepository, never()).findById(anyLong());
        verify(itemRepository, never()).search(anyString(), any(Pageable.class));
    }

    @Test
    void shouldGetEmptyListWithSearchWithBlankText() {
        when(userRepository.existsById(anyLong()))
                .thenReturn(true);
        when(itemRepository.search(anyString(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(items));

        List<ItemDto> items = itemService.search(1L, " ", 7, 3);

        assertThat(items).asList()
                .isEmpty();
        verify(userRepository, times(1)).existsById(anyLong());
        verify(itemRequestRepository, never()).findById(anyLong());
        verify(commentRepository, never()).findById(anyLong());
        verify(itemRepository, never()).search(anyString(), any(Pageable.class));
    }

    @Test
    void shouldCreateComment() {
        item.setBookings(Set.of(booking));

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user2));
        when(itemRepository.findByIdWithOwner(anyLong()))
                .thenReturn(Optional.of(item));
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);

        itemService.addComment(2L, 1L, creatingCommentDto);

        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, never()).findById(anyLong());
        verify(commentRepository, never()).findById(anyLong());
        verify(itemRepository, times(1)).findByIdWithOwner(anyLong());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void shouldGetExceptionWithCreateCommentWithNotFoundUser() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.empty());
        when(itemRepository.findByIdWithOwner(anyLong()))
                .thenReturn(Optional.of(item));
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);

        assertThrows(EntityNotFoundException.class, () -> itemService.addComment(1L, 1L, creatingCommentDto));
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, never()).findById(anyLong());
        verify(commentRepository, never()).findById(anyLong());
        verify(itemRepository, never()).findById(anyLong());
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void shouldGetExceptionWithCreateCommentWithNotFoundItem() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRepository.findByIdWithOwner(anyLong()))
                .thenReturn(Optional.empty());
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);

        assertThrows(EntityNotFoundException.class, () -> itemService.addComment(1L, 1L, creatingCommentDto));
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, never()).findById(anyLong());
        verify(commentRepository, never()).findById(anyLong());
        verify(itemRepository, times(1)).findByIdWithOwner(anyLong());
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    void shouldGetExceptionWithCreateCommentWithNotFoundBooking() {
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user2));
        when(itemRepository.findByIdWithOwner(anyLong()))
                .thenReturn(Optional.of(item));
        when(commentRepository.save(any(Comment.class)))
                .thenReturn(comment);

        assertThrows(NotAvailableBookingException.class, () -> itemService.addComment(1L, 1L, creatingCommentDto));
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, never()).findById(anyLong());
        verify(commentRepository, never()).findById(anyLong());
        verify(itemRepository, times(1)).findByIdWithOwner(anyLong());
        verify(commentRepository, never()).save(any(Comment.class));
    }
}

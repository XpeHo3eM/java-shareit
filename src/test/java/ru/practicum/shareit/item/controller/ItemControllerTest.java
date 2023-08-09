package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dal.ItemService;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CreatingCommentDto;
import ru.practicum.shareit.item.dto.item.CreatingItemDto;
import ru.practicum.shareit.item.dto.item.ItemDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.util.Constant.HEADER_USER_ID;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    ObjectMapper mapper;

    @Autowired
    MockMvc mvc;

    @MockBean
    ItemService service;

    private final CreatingItemDto creatingItemDto = CreatingItemDto.builder()
            .name("name")
            .description("description")
            .available(true)
            .build();

    private final ItemDto itemDto = ItemDto.builder()
            .id(1L)
            .name("name")
            .description("description")
            .available(true)
            .build();

    private final ItemDto itemDto2 = ItemDto.builder()
            .id(2L)
            .name("name2")
            .description("description2")
            .available(true)
            .build();

    private final CreatingCommentDto creatingCommentDto = CreatingCommentDto.builder()
            .text("comment")
            .build();

    private final CommentDto commentDto = CommentDto.builder()
            .id(1L)
            .text("comment")
            .authorName("name")
            .created(LocalDateTime.now())
            .build();

    @Test
    void shouldGetExceptionWithoutHeader() throws Exception {
        mvc.perform(post("/items")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, never()).addItem(anyLong(), any(CreatingItemDto.class));

        mvc.perform(patch("/items/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, never()).updateItem(anyLong(), anyLong(), any(CreatingItemDto.class));

        mvc.perform(delete("/items/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, never()).deleteItem(anyLong(), anyLong());

        mvc.perform(get("/items")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, never()).getAllItemsByUserId(anyLong(), any(Pageable.class));

        mvc.perform(get("/items/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, never()).getItemById(anyLong(), anyLong());

        mvc.perform(get("/items/search?text=search")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, never()).search(anyLong(), anyString(), any(Pageable.class));

        mvc.perform(post("/items/1/comment")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, never()).addComment(anyLong(), anyLong(), any(CreatingCommentDto.class));
    }

    @Test
    void shouldGetExceptionWithCreateItemWithoutName() throws Exception {
        CreatingItemDto incorrectCreatingItemDto = CreatingItemDto.builder()
                .description("description")
                .available(true)
                .build();
        String jsonItem = mapper.writeValueAsString(incorrectCreatingItemDto);

        mvc.perform(post("/items")
                        .header(HEADER_USER_ID, 1L)
                        .content(jsonItem)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, never()).addItem(anyLong(), any(CreatingItemDto.class));
    }

    @Test
    void shouldGetExceptionWithCreateItemWithoutDescription() throws Exception {
        CreatingItemDto incorrectCreatingItemDto = CreatingItemDto.builder()
                .name("item")
                .available(true)
                .build();
        String jsonItem = mapper.writeValueAsString(incorrectCreatingItemDto);

        mvc.perform(post("/items")
                        .header(HEADER_USER_ID, 1L)
                        .content(jsonItem)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, never()).addItem(anyLong(), any(CreatingItemDto.class));
    }

    @Test
    void shouldGetExceptionWithCreateItemWithoutAvailable() throws Exception {
        CreatingItemDto incorrectCreatingItemDto = CreatingItemDto.builder()
                .name("item")
                .description("description")
                .build();
        String jsonItem = mapper.writeValueAsString(incorrectCreatingItemDto);

        mvc.perform(post("/items")
                        .header(HEADER_USER_ID, 1L)
                        .content(jsonItem)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, never()).addItem(anyLong(), any(CreatingItemDto.class));
    }

    @Test
    void shouldCreateItem() throws Exception {
        when(service.addItem(anyLong(), any(CreatingItemDto.class)))
                .thenReturn(itemDto);

        String jsonItem = mapper.writeValueAsString(creatingItemDto);

        mvc.perform(post("/items")
                        .header(HEADER_USER_ID, 1L)
                        .content(jsonItem)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
        verify(service, times(1)).addItem(anyLong(), any(CreatingItemDto.class));
    }

    @Test
    void shouldUpdateItem() throws Exception {
        when(service.updateItem(anyLong(), anyLong(), any(CreatingItemDto.class)))
                .thenReturn(itemDto);

        String jsonItem = mapper.writeValueAsString(creatingItemDto);

        mvc.perform(patch("/items/1")
                        .header(HEADER_USER_ID, 1L)
                        .content(jsonItem)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
        verify(service, times(1)).updateItem(anyLong(), anyLong(), any(CreatingItemDto.class));
    }

    @Test
    void shouldDeleteItem() throws Exception {
        mvc.perform(delete("/items/1")
                        .header(HEADER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(service, times(1)).deleteItem(anyLong(), anyLong());
    }

    @Test
    void shouldGetAllByUserId() throws Exception {
        when(service.getAllItemsByUserId(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(itemDto, itemDto2));

        mvc.perform(get("/items")
                        .header(HEADER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].name", is("name")))
                .andExpect(jsonPath("$[0].description", is("description")))
                .andExpect(jsonPath("$[0].available", is(true)))
                .andExpect(jsonPath("$[1].id", is(2L), Long.class))
                .andExpect(jsonPath("$[1].name", is("name2")))
                .andExpect(jsonPath("$[1].description", is("description2")))
                .andExpect(jsonPath("$[1].available", is(true)));
        verify(service, times(1)).getAllItemsByUserId(anyLong(), any(Pageable.class));
    }

    @Test
    void shouldGetByItemId() throws Exception {
        when(service.getItemById(anyLong(), anyLong()))
                .thenReturn(itemDto);

        mvc.perform(get("/items/1")
                        .header(HEADER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));
        verify(service, times(1)).getItemById(anyLong(), anyLong());
    }

    @Test
    void shouldGetExceptionWithSearchWithoutText() throws Exception {
        mvc.perform(get("/items/search")
                        .header(HEADER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, never()).search(anyLong(), anyString(), any(Pageable.class));
    }

    @Test
    void shouldSearch() throws Exception {
        when(service.search(anyLong(), anyString(), any(Pageable.class)))
                .thenReturn(List.of(itemDto, itemDto2));

        mvc.perform(get("/items/search?text=search")
                        .header(HEADER_USER_ID, 1L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1L), Long.class))
                .andExpect(jsonPath("$[0].name", is("name")))
                .andExpect(jsonPath("$[0].description", is("description")))
                .andExpect(jsonPath("$[0].available", is(true)))
                .andExpect(jsonPath("$[1].id", is(2L), Long.class))
                .andExpect(jsonPath("$[1].name", is("name2")))
                .andExpect(jsonPath("$[1].description", is("description2")))
                .andExpect(jsonPath("$[1].available", is(true)));
        verify(service, times(1)).search(anyLong(), anyString(), any(Pageable.class));
    }

    @Test
    void shouldGetExceptionWithCommentWithBlankText() throws Exception {
        CreatingCommentDto creatingCommentDtoIncorrect = CreatingCommentDto.builder()
                .text(" ")
                .build();
        String jsonComment = mapper.writeValueAsString(creatingCommentDtoIncorrect);

        mvc.perform(post("/items/1/comment")
                        .header(HEADER_USER_ID, 1L)
                        .content(jsonComment)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, never()).addComment(anyLong(), anyLong(), any(CreatingCommentDto.class));
    }

    @Test
    void shouldCreateComment() throws Exception {
        when(service.addComment(anyLong(), anyLong(), any(CreatingCommentDto.class)))
                .thenReturn(commentDto);

        String jsonComment = mapper.writeValueAsString(creatingCommentDto);

        mvc.perform(post("/items/1/comment")
                        .header(HEADER_USER_ID, 1L)
                        .content(jsonComment)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(service, times(1)).addComment(anyLong(), anyLong(), any(CreatingCommentDto.class));
    }
}

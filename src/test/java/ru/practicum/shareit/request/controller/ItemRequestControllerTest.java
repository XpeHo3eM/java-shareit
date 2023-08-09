package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dal.ItemRequestService;
import ru.practicum.shareit.request.dto.CreatingItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.util.Constant.HEADER_USER_ID;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private ItemRequestService service;

    private final CreatingItemRequestDto correctRequest = CreatingItemRequestDto.builder()
            .description("description")
            .build();
    private final CreatingItemRequestDto requestWithBlankDescription = CreatingItemRequestDto.builder()
            .description(" ")
            .build();
    private final ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .id(1L)
            .description("description")
            .build();
    private final ItemRequestDto itemRequestDto2 = ItemRequestDto.builder()
            .id(2L)
            .description("description2")
            .build();
    private final List<ItemRequestDto> listOfRequests = List.of(itemRequestDto, itemRequestDto2);


    @Test
    void shouldExceptionWithCreateRequestWithoutHeader() throws Exception {
        when(service.addItemRequest(anyLong(), any(CreatingItemRequestDto.class)))
                .thenReturn(itemRequestDto);

        String jsonRequest = mapper.writeValueAsString(correctRequest);

        mvc.perform(post("/requests")
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, never()).addItemRequest(anyLong(), any(CreatingItemRequestDto.class));
    }

    @Test
    void shouldExceptionWithCreateRequestWithRequestWithBlankDescription() throws Exception {
        when(service.addItemRequest(anyLong(), any(CreatingItemRequestDto.class)))
                .thenReturn(itemRequestDto);

        String jsonRequest = mapper.writeValueAsString(requestWithBlankDescription);

        mvc.perform(post("/requests")
                        .header(HEADER_USER_ID, 1)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, never()).addItemRequest(anyLong(), any(CreatingItemRequestDto.class));
    }

    @Test
    void shouldCreateRequest() throws Exception {
        when(service.addItemRequest(anyLong(), any(CreatingItemRequestDto.class)))
                .thenReturn(itemRequestDto);

        String jsonRequest = mapper.writeValueAsString(correctRequest);

        mvc.perform(post("/requests")
                        .header(HEADER_USER_ID, 1)
                        .content(jsonRequest)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));
        verify(service, times(1)).addItemRequest(anyLong(), any(CreatingItemRequestDto.class));
    }

    @Test
    void shouldExceptionWithGetAllRequestsByUserIdWithRequestWithoutHeader() throws Exception {
        when(service.getOwnerItemRequests(anyLong()))
                .thenReturn(listOfRequests);

        mvc.perform(get("/requests")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, never()).getOwnerItemRequests(anyLong());
    }

    @Test
    void shouldGetAllRequestsByUserId() throws Exception {
        when(service.getOwnerItemRequests(anyLong()))
                .thenReturn(listOfRequests);

        mvc.perform(get("/requests")
                        .header(HEADER_USER_ID, 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
        verify(service, times(1)).getOwnerItemRequests(anyLong());
    }

    @Test
    void shouldExceptionWithGetAllRequestsWithRequestWithoutHeader() throws Exception {
        when(service.getAllItemRequests(anyLong(), any(Pageable.class)))
                .thenReturn(listOfRequests);

        mvc.perform(get("/requests/all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, never()).getAllItemRequests(anyLong(), any(Pageable.class));
    }

    @Test
    void shouldExceptionWithGetAllRequestsWithFromMoreThenMaxInt() throws Exception {
        when(service.getAllItemRequests(anyLong(), any(Pageable.class)))
                .thenReturn(listOfRequests);

        mvc.perform(get("/requests/all?from=2147483648")
                        .header(HEADER_USER_ID, 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, never()).getAllItemRequests(anyLong(), any(Pageable.class));
    }

    @Test
    void shouldGetAllRequests() throws Exception {
        when(service.getAllItemRequests(anyLong(), any(Pageable.class)))
                .thenReturn(listOfRequests);

        mvc.perform(get("/requests/all")
                        .header(HEADER_USER_ID, 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[1].id", is(2)));
        verify(service, times(1)).getAllItemRequests(anyLong(), any(Pageable.class));
    }

    @Test
    void shouldExceptionWithGetRequestByIdWithRequestWithoutHeader() throws Exception {
        when(service.getItemRequest(anyLong(), anyLong()))
                .thenReturn(itemRequestDto);

        mvc.perform(get("/requests/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(service, never()).getItemRequest(anyLong(), anyLong());
    }

    @Test
    void shouldGetRequestById() throws Exception {
        when(service.getItemRequest(anyLong(), anyLong()))
                .thenReturn(itemRequestDto);

        mvc.perform(get("/requests/1")
                        .header(HEADER_USER_ID, 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId().intValue())))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));
        verify(service, times(1)).getItemRequest(anyLong(), anyLong());
    }
}
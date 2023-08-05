package ru.practicum.shareit.request.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dal.ItemRequestService;
import ru.practicum.shareit.request.dto.CreatingItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Transactional
@Slf4j
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestIntegrationTest {
    private final ItemRequestService requestService;
    private final UserRepository userStorage;
    private final EntityManager entityManager;

    private final User user = User.builder()
            .id(1L)
            .name("userName")
            .email("mail@ya.ru")
            .build();
    private final User user2 = User.builder()
            .id(2L)
            .name("userName")
            .email("mail2@ya.ru")
            .build();
    private final CreatingItemRequestDto requestDto = CreatingItemRequestDto.builder()
            .description("description")
            .build();
    private final CreatingItemRequestDto requestDto2 = CreatingItemRequestDto.builder()
            .description("description2")
            .build();
    private final CreatingItemRequestDto requestDto3 = CreatingItemRequestDto.builder()
            .description("description3")
            .build();

    @BeforeEach
    void setup() {
        userStorage.save(user);
        requestService.addItemRequest(user.getId(), requestDto);
        requestService.addItemRequest(user.getId(), requestDto2);
        userStorage.save(user2);
        requestService.addItemRequest(user2.getId(), requestDto3);
    }

    @Test
    void shouldCreateRequest() {
        ItemRequest request = entityManager.createQuery("SELECT ir" +
                        " FROM ItemRequest AS ir" +
                        " WHERE ir.id = :id", ItemRequest.class)
                .setParameter("id", 1L)
                .getSingleResult();

        assertThat(request)
                .hasFieldOrPropertyWithValue("description", requestDto.getDescription())
                .hasFieldOrPropertyWithValue("requester", user);
    }

    @Test
    void shouldGetRequestById() {
        ItemRequestDto request = requestService.getItemRequest(user.getId(), 1L);

        assertThat(request)
                .hasFieldOrPropertyWithValue("description", requestDto.getDescription())
                .hasFieldOrPropertyWithValue("items", Collections.emptyList());
    }

    @Test
    void shouldGetAllRequests() throws InterruptedException {
        List<ItemRequestDto> requests = requestService.getAllItemRequests(2L, 0, 5);

        assertThat(requests).asList()
                .hasSize(2)
                .satisfies(list -> {
                    assertThat(list.get(0))
                            .hasFieldOrPropertyWithValue("id", 2L)
                            .hasFieldOrPropertyWithValue("description", requestDto2.getDescription());
                    assertThat(list.get(1))
                            .hasFieldOrPropertyWithValue("id", 1L)
                            .hasFieldOrPropertyWithValue("description", requestDto.getDescription());
                });
    }

    @Test
    void shouldGetAllRequestsByUserId() throws InterruptedException {
        List<ItemRequestDto> requests1 = requestService.getOwnerItemRequests(1L);

        assertThat(requests1).asList()
                .hasSize(2)
                .satisfies(list -> {
                    assertThat(list.get(0))
                            .hasFieldOrPropertyWithValue("id", 2L)
                            .hasFieldOrPropertyWithValue("description", requestDto2.getDescription());
                    assertThat(list.get(1))
                            .hasFieldOrPropertyWithValue("id", 1L)
                            .hasFieldOrPropertyWithValue("description", requestDto.getDescription());
                });
    }
}

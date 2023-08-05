package ru.practicum.shareit.item.dao;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dal.ItemService;
import ru.practicum.shareit.item.dto.item.CreatingItemDto;
import ru.practicum.shareit.item.dto.item.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dal.UserService;
import ru.practicum.shareit.user.dto.CreatingUserDto;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemIntegrationTest {
    private final ItemService itemService;
    private final UserService userService;
    private final EntityManager entityManager;

    private final CreatingItemDto creatingItemDto = CreatingItemDto.builder()
            .name("itemName")
            .description("itemDescription")
            .available(true)
            .build();
    private final CreatingUserDto creatingUserDto = CreatingUserDto.builder()
            .name("userName")
            .email("user@ya.ru")
            .build();

    @Test
    void shouldAddItem() {
        userService.addUser(creatingUserDto);
        itemService.addItem(1L, creatingItemDto);

        Item itemFromService = entityManager.createQuery(getQueryGetItemById(), Item.class)
                .setParameter("id", 1L)
                .getSingleResult();

        assertThat(itemFromService)
                .hasFieldOrPropertyWithValue("name", creatingItemDto.getName())
                .hasFieldOrPropertyWithValue("description", creatingItemDto.getDescription())
                .hasFieldOrPropertyWithValue("available", creatingItemDto.getAvailable());
    }

    @Test
    void shouldUpdateItem() {
        userService.addUser(creatingUserDto);
        itemService.addItem(1L, creatingItemDto);

        CreatingItemDto updatedItemDto = CreatingItemDto.builder()
                .name("updated")
                .description("updated")
                .available(false)
                .build();

        itemService.updateItem(1L, 1L, updatedItemDto);

        Item itemFromService = entityManager.createQuery(getQueryGetItemById(), Item.class)
                .setParameter("id", 1L)
                .getSingleResult();

        assertThat(itemFromService)
                .hasFieldOrPropertyWithValue("name", updatedItemDto.getName())
                .hasFieldOrPropertyWithValue("description", updatedItemDto.getDescription())
                .hasFieldOrPropertyWithValue("available", updatedItemDto.getAvailable());
    }

    @Test
    void shouldDeleteItem() {
        userService.addUser(creatingUserDto);
        itemService.addItem(1L, creatingItemDto);

        Item itemFromService = entityManager.createQuery(getQueryGetItemById(), Item.class)
                .setParameter("id", 1L)
                .getSingleResult();

        assertThat(itemFromService).isNotNull();

        itemService.deleteItem(1L, 1L);

        itemFromService = entityManager.createQuery(getQueryGetItemById(), Item.class)
                .setParameter("id", 1L)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);

        assertThat(itemFromService).isNull();
    }

    @Test
    void shouldGetItemById() {
        userService.addUser(creatingUserDto);
        itemService.addItem(1L, creatingItemDto);

        ItemDto itemFromService = itemService.getItemById(1L, 1L);

        assertThat(itemFromService)
                .hasFieldOrPropertyWithValue("name", creatingItemDto.getName())
                .hasFieldOrPropertyWithValue("description", creatingItemDto.getDescription())
                .hasFieldOrPropertyWithValue("available", creatingItemDto.getAvailable())
                .hasFieldOrPropertyWithValue("requestId", creatingItemDto.getRequestId());
    }

    @Test
    void shouldGetAllItemsByUserId() {
        CreatingItemDto creatingItemDto2 = CreatingItemDto.builder()
                .name("itemName2")
                .description("itemDescription2")
                .available(false)
                .build();

        userService.addUser(creatingUserDto);

        itemService.addItem(1L, creatingItemDto);
        itemService.addItem(1L, creatingItemDto2);

        List<ItemDto> items = itemService.getAllItemsByUserId(1L, 0, 10);

        assertThat(items).asList()
                .hasSize(2)
                .satisfies(list -> {
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 1L);
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("name", creatingItemDto.getName());
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("description", creatingItemDto.getDescription());
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("available", creatingItemDto.getAvailable());
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("requestId", creatingItemDto.getRequestId());
                    assertThat(list.get(1)).hasFieldOrPropertyWithValue("id", 2L);
                    assertThat(list.get(1)).hasFieldOrPropertyWithValue("name", creatingItemDto2.getName());
                    assertThat(list.get(1)).hasFieldOrPropertyWithValue("description", creatingItemDto2.getDescription());
                    assertThat(list.get(1)).hasFieldOrPropertyWithValue("available", creatingItemDto2.getAvailable());
                    assertThat(list.get(1)).hasFieldOrPropertyWithValue("requestId", creatingItemDto2.getRequestId());
                });
    }

    @Test
    void shouldSearch() {
        CreatingItemDto creatingItemDto2 = CreatingItemDto.builder()
                .name("itemName2")
                .description("itemDescription2")
                .available(false)
                .build();
        CreatingItemDto creatingItemDto3 = CreatingItemDto.builder()
                .name("itemName3")
                .description("itemDescription2 return")
                .available(true)
                .build();

        userService.addUser(creatingUserDto);

        itemService.addItem(1L, creatingItemDto);
        itemService.addItem(1L, creatingItemDto2);
        itemService.addItem(1L, creatingItemDto3);

        List<ItemDto> items = itemService.search(1L, "Description2", 0, 10);

        assertThat(items).asList()
                .hasSize(1)
                .satisfies(list -> {
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("id", 3L);
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("name", creatingItemDto3.getName());
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("description", creatingItemDto3.getDescription());
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("available", creatingItemDto3.getAvailable());
                    assertThat(list.get(0)).hasFieldOrPropertyWithValue("requestId", creatingItemDto3.getRequestId());
                });
    }

    private String getQueryGetItemById() {
        return "SELECT i " +
                " FROM Item AS i" +
                " WHERE i.id = :id";
    }

    private String getQueryGetAllItems() {
        return "SELECT i " +
                " FROM Item AS i";
    }
}

package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.comment.CreatingCommentDto;
import ru.practicum.shareit.item.dto.item.CreatingItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> addItem(long userId, CreatingItemDto creatingItemDto) {
        return post("", userId, creatingItemDto);
    }

    public ResponseEntity<Object> updateItem(long userId, long itemId, CreatingItemDto creatingItemDto) {
        return patch("/" + itemId, userId, creatingItemDto);
    }

    public ResponseEntity<Object> getItemById(long userId, long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getOwnerItems(long userId, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size);
        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> findItem(long userId, String text, int from, int size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size);
        return get("/search?text={text}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> addComment(long userId, long itemId, CreatingCommentDto creatingCommentDto) {
        return post("/" + itemId + "/comment", userId, creatingCommentDto);
    }

    public void delete(long userId, long itemId) {
        delete("/" + itemId, userId);
    }
}

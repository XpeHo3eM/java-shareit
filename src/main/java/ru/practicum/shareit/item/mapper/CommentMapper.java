package ru.practicum.shareit.item.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CreatingCommentDto;
import ru.practicum.shareit.item.model.Comment;

@UtilityClass
public class CommentMapper {
    public CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .created(comment.getCreationDate())
                .authorName(comment.getAuthor().getName())
                .build();
    }

    public Comment toComment(CreatingCommentDto creatingCommentDto) {
        return Comment.builder()
                .text(creatingCommentDto.getText())
                .build();
    }
}

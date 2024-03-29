package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.comment.CommentDto;
import ru.practicum.shareit.item.dto.comment.CreatingCommentDto;
import ru.practicum.shareit.item.model.Comment;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "created", source = "comment.creationDate")
    @Mapping(target = "authorName", source = "comment.author.name")
    CommentDto toDto(Comment comment);

    Comment toComment(CreatingCommentDto creatingCommentDto);
}

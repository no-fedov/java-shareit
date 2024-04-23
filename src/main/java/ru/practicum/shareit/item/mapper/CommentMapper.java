package ru.practicum.shareit.item.mapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CommentMapper {
    public static Comment mapToCommentFromCommentCreatedDto(CommentCreateDto commentCreateDto, User user, Item item) {
        return Comment.builder()
                .author(user)
                .item(item)
                .text(commentCreateDto.getText())
                .created(commentCreateDto.getCreated())
                .build();
    }

    public static CommentDto mapToCommentDtoFromComment(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .authorName(comment.getAuthor().getName())
                .text(comment.getText())
                .created(comment.getCreated())
                .build();
    }

}

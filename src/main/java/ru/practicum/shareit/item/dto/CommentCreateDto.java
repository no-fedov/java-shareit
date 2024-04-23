package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder(toBuilder = true)
public class CommentCreateDto {
    private int authorId;
    private int itemId;
    @NotBlank
    private String text;
    private LocalDateTime created;
}

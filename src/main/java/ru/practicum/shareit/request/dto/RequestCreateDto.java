package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;


@Data
@Builder(toBuilder = true)
public class RequestCreateDto {
    @NotBlank
    private String description;
    private int requester;
    private LocalDateTime created;
}

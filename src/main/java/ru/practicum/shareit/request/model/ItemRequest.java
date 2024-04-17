package ru.practicum.shareit.request.model;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@Builder(toBuilder = true)
public class ItemRequest {
    private final int id;
    @NotNull
    private final String description;
    private final int requester;
    @NotNull
    private final LocalDateTime created;
}

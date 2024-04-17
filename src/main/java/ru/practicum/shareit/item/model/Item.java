package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.practicum.shareit.request.model.ItemRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@RequiredArgsConstructor
@Builder(toBuilder = true)
public class Item {
    private final int id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Status available;
    private final int owner;
    @NotNull
    private ItemRequest request;

    public enum Status {
        ACTIVE,
        INACTIVE
    }
}

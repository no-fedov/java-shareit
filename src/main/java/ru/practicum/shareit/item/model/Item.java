package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder(toBuilder = true)
public class Item {
    private final int id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    private boolean available;
    private final int owner;
    @NotNull
    private ItemRequest request;

    public enum Status {
        ACTIVE,
        INACTIVE
    }
}

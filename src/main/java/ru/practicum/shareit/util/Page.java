package ru.practicum.shareit.util;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Page {
    public static Pageable getPage(Integer from, Integer size) {
        Pageable page = Pageable.unpaged();
        if (from != null && size != null) {
            page = PageRequest.of(from / size, size);
        }
        return page;
    }
}

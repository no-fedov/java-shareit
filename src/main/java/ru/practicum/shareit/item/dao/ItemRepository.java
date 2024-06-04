package ru.practicum.shareit.item.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.dto.ItemPresentForRequestDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    Optional<Item> findByIdAndOwnerId(int itemId, int ownerID);

    Page<Item> findByOwnerIdOrderByIdAsc(int ownerID, Pageable page);

    @Query("SELECT i FROM Item AS i " +
            "WHERE (lower(i.name) like lower(concat('%', :text, '%')) " +
            "OR lower(i.description) like lower(concat('%', :text, '%'))) " +
            "AND i.available =:condition")
    Page<Item> findByNameOrDescription(String text, boolean condition, Pageable page);

    @Query("SELECT new ru.practicum.shareit.item.dto.ItemPresentForRequestDto(i.id, i.description, r.id , i.available) " +
            "FROM Item i " +
            "JOIN i.request r " +
            "WHERE r.id IN :requestId")
    List<ItemPresentForRequestDto> findItemsByRequestIds(Collection<Integer> requestId);
}

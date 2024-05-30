package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    Optional<Item> findByIdAndOwnerId(int itemId, int ownerID);

    List<Item> findByOwnerIdOrderByIdAsc(int ownerID);

    @Query("SELECT i FROM Item AS i " +
            "WHERE (lower(i.name) like lower(concat('%', :text, '%')) " +
            "OR lower(i.description) like lower(concat('%', :text, '%'))) " +
            "AND i.available =:condition")
    List<Item> findByNameOrDescription(String text, boolean condition);

}

package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Integer> {
    Optional<Item> findByIdAndOwnerId(int itemId, int ownerID);

    List<Item> findByOwnerIdOrderByIdAsc(int ownerID);

    List<Item> findByNameOrDescriptionContainingAllIgnoreCaseAndAvailable(String inName, String inDescription, boolean condition);

}

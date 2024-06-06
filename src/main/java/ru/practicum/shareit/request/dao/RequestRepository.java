package ru.practicum.shareit.request.dao;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Integer> {
    List<Request> findByRequesterIdOrderByCreatedDesc(int requesterId);

    List<Request> findByRequesterIdNot(int userId, Pageable page);

}

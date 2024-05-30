package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Integer> {
    @Query("select new ru.practicum.shareit.item.dto.CommentDto(cm.id, cm.text, u.name, cm.created) " +
            "from Comment cm " +
            "join cm.item i " +
            "join cm.author u " +
            "where i.id = :itemId")
    List<CommentDto> findCommentsByItemId(@Param("itemId") int itemId);

}
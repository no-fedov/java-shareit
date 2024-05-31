package ru.practicum.shareit.item.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.jdbc.Sql;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.ItemUtil;
import ru.practicum.shareit.util.UserUtil;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Sql(scripts = "/schema-test.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
public class CommentRepositoryTest {
    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ItemRepository itemRepository;

    private Item item;

    @BeforeEach
    public void setUp() {
        userRepository.saveAll(List.of(UserUtil.getUser1(), UserUtil.getUser2()));
        item = itemRepository.save(ItemUtil.getItem2_WhereOwnerUser2());
    }

    @Test
    @DisplayName("Test save comment functionality")
    public void givenComment_whenSave_thenReturnComment() {
        //given
        User user = UserUtil.getUser1();

        Comment comment = Comment.builder()
                .item(item)
                .text("comment")
                .author(user)
                .build();

        //when
        Comment savedComment = commentRepository.save(comment);

        //then
        assertThat(savedComment).isNotNull();
    }

    @Test
    @DisplayName("Test get comments by item id functionality")
    public void givenComment_whenSave_thenReturnCommentDto() {
        //given
        User user = UserUtil.getUser1();

        Comment comment = Comment.builder()
                .item(item)
                .text("comment")
                .author(user)
                .build();

        Comment savedComment = commentRepository.save(comment);

        //when
        List<CommentDto> commentDto = commentRepository.findCommentsByItemId(item.getId());

        //then
        assertThat(commentDto.size()).isEqualTo(1);
    }
}

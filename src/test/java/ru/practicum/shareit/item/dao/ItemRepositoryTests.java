package ru.practicum.shareit.item.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.util.CollectionUtils;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.UserUtil;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ItemRepositoryTests {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;

    User createdUser;

    @BeforeEach
    public void setUp() {
        itemRepository.deleteAll();
        userRepository.deleteAll();
        createdUser = userRepository.save(UserUtil.getUser1());
    }

    @Test
    @DisplayName("Test save item functionality")
    public void givenItem_whenSave_thenItemIsCreated() {
        //given
        Item item = Item.builder()
                .name("item1")
                .owner(createdUser)
                .description("description item 1")
                .available(true)
                .build();

        //when
        Item savedItem = itemRepository.save(item);

        //then
        assertThat(savedItem).isNotNull();
        assertThat(savedItem.getId()).isEqualTo(1);
    }

    @Test
    @DisplayName("Test update item set available = false")
    public void givenAvailableItem_whenUpdateItem_ThenReturnedUnavailableItem() {

        //given
        Item item = Item.builder()
                .name("item1")
                .owner(createdUser)
                .description("description item 1")
                .available(true)
                .build();

        Item savedItem = itemRepository.save(item);
        savedItem.setAvailable(false);

        //when
        Item updatedItem = itemRepository.save(savedItem);

        //then
        assertThat(updatedItem.getAvailable()).isFalse();
    }

    @Test
    @DisplayName("Test get items by owner id")
    public void givenCreatedItem_whenFindByOwnerId_thenListItemSize1() {
        //given
        Item item = Item.builder()
                .name("item1")
                .owner(createdUser)
                .description("description item 1")
                .available(true)
                .build();

        Item savedItem = itemRepository.save(item);

        //when
        List<Item> obtainedItems = itemRepository.findByOwnerIdOrderByIdAsc(createdUser.getId());

        //then
        assertThat(CollectionUtils.isEmpty(obtainedItems)).isFalse();
        assertThat(obtainedItems.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("Test get item by id and owner id")
    public void givenCreatedItem_whenFindByIdAndOwnerId() {

        //given
        Item item = Item.builder()
                .name("item1")
                .owner(createdUser)
                .description("description item 1")
                .available(true)
                .build();

        Item savedItem = itemRepository.save(item);

        //when
        Optional<Item> obtainedItem = itemRepository.findByIdAndOwnerId(1, 1);

        //then
        assertThat(obtainedItem.isPresent()).isTrue();
        assertThat(obtainedItem.get()).isEqualTo(savedItem);
    }

    @Test
    @DisplayName("Test get items by description and available")
    public void givenThreeItem_whenFindItemByDescriptionOrName_thenReturnedTwoItem() {

        //given
        Item item1 = Item.builder()
                .name("item1")
                .owner(createdUser)
                .description("дреЛь item 1")
                .available(true)
                .build();

        Item item2 = Item.builder()
                .name("item2")
                .owner(createdUser)
                .description("дрель item 2")
                .available(false)
                .build();

        Item item3 = Item.builder()
                .name("ДРЕЛЬ item3")
                .owner(createdUser)
                .description("description item 3")
                .available(true)
                .build();

        itemRepository.saveAll(List.of(item1, item2, item3));

        //when
        String textToFind = "дреЛЬ";

        List<Item> obtainedItems = itemRepository.findByNameOrDescription(textToFind, true);

        //then
        assertThat(CollectionUtils.isEmpty(obtainedItems)).isFalse();
        assertThat(obtainedItems.size()).isEqualTo(2);
    }
}

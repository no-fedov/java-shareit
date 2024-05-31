package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.util.CollectionUtils;
import ru.practicum.shareit.booking.dao.BookingOwnerRepository;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exception.model.NotFoundEntityException;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.exception.CommentException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.ItemUtil;
import ru.practicum.shareit.util.UserUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemServiceTests {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingOwnerRepository bookingOwnerRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserService userService;
    @Mock
    private ItemOwnerService itemOwnerService;

    @InjectMocks
    private ItemServiceImp itemService;

    @Test
    @DisplayName("Test add item functionality")
    public void givenItemCreateDto_thenSaveItem_thenReturnSaverItem() {
        //given
        User user1 = UserUtil.getUser1();
        Item item1 = ItemUtil.getItem1_WhereOwnerUser1();

        int ownerId = user1.getId();

        ItemCreateDto itemCreateDto = ItemCreateDto.builder()
                .name("Item1")
                .owner(ownerId)
                .description("Item1")
                .available(true)
                .build();

        Mockito.when(userService.getCurrentUserById(anyInt())).thenReturn(user1);
        Mockito.when(itemRepository.save(any(Item.class))).thenReturn(item1);


        //when
        ItemDto newItem = itemService.addItem(itemCreateDto);

        //then
        assertThat(newItem).isNotNull();
        assertThat(newItem.getName()).isEqualTo(itemCreateDto.getName());
        verify(userService, times(1)).getCurrentUserById(anyInt());
        verify(itemRepository, times(1)).save(any(Item.class));

    }

    @Test
    @DisplayName("Test get item there user is not owner")
    public void givenUserAndItem_whenFindItemById_ReturnItemDto() {
        //given
        User user2 = UserUtil.getUser2();

        Item item1 = ItemUtil.getItem1_WhereOwnerUser1();

        int currentUserId = user2.getId();
        int itemId = item1.getId();
        CommentDto commentDto = new CommentDto();
        Mockito.when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item1));
        Mockito.when(commentRepository.findCommentsByItemId(anyInt())).thenReturn(List.of(commentDto));

        //when
        ItemPresentDto itemPresentDto = itemService.findItem(currentUserId, itemId);

        //then
        assertThat(itemPresentDto).isNotNull();
        assertThat(CollectionUtils.isEmpty(itemPresentDto.getComments())).isFalse();
        assertThat(itemPresentDto.getLastBooking()).isNull();
        assertThat(itemPresentDto.getNextBooking()).isNull();
    }

    @Test
    @DisplayName("Test get item there user is owner")
    public void givenOwnerAndItem_whenFindItemById_ReturnItemDto() {
        //given
        User user1 = UserUtil.getUser1();
        User user2 = UserUtil.getUser2();

        int currentUserId = user1.getId();

        Item item1 = ItemUtil.getItem1_WhereOwnerUser1();
        int itemId = item1.getId();
        CommentDto commentDto = new CommentDto();
        Booking prev = Booking.builder()
                .start(LocalDateTime.of(2222, 12, 1, 1, 1))
                .booker(user2)
                .status(Status.WAITING)
                .end(LocalDateTime.of(2223, 12, 1, 1, 1))
                .item(item1)
                .build();

        Booking next = Booking.builder()
                .start(LocalDateTime.of(2223, 12, 1, 1, 1))
                .booker(user2)
                .status(Status.WAITING)
                .end(LocalDateTime.of(2224, 12, 1, 1, 1))
                .item(item1)
                .build();

        Mockito.when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item1));
        Mockito.when(commentRepository.findCommentsByItemId(anyInt())).thenReturn(List.of(commentDto));
        Mockito.when(bookingOwnerRepository.findPreviousBooking(anyInt(), any(LocalDateTime.class))).thenReturn(prev);
        Mockito.when(bookingOwnerRepository.findNextBooking(anyInt(), any(LocalDateTime.class))).thenReturn(next);

        //when
        ItemPresentDto itemPresentDto = itemService.findItem(currentUserId, itemId);

        //then
        assertThat(itemPresentDto).isNotNull();
        assertThat(CollectionUtils.isEmpty(itemPresentDto.getComments())).isFalse();
        assertThat(itemPresentDto.getLastBooking()).isNotNull();
        assertThat(itemPresentDto.getNextBooking()).isNotNull();
    }

    @Test
    @DisplayName("Test get exist item")
    public void givenItemIdAndUserId_whenFindItem_thenExistIsThrow() {

        //given
        int itemId = 1;
        int userId = 1;
        Mockito.when(itemRepository.findById(anyInt())).thenReturn(Optional.empty());

        //when

        //then
        NotFoundEntityException exception = assertThrows(NotFoundEntityException.class,
                () -> itemService.findItem(userId, itemId));

        assertThat(exception.getMessage()).isEqualTo("Item not found");
    }

    @Test
    @DisplayName("Test get user items")
    public void givenThreeItem_whenGetUserItems_whenReturnThreeItems() {

        //given
        List<ItemPresentDto> items = List.of(ItemPresentDto.builder().build(),
                ItemPresentDto.builder().build(),
                ItemPresentDto.builder().build());
        Mockito.when(itemOwnerService.getUserItems(anyInt())).thenReturn(items);

        //when
        List<ItemPresentDto> obtainedItems = itemService.getUserItems(1);

        //then
        assertThat(CollectionUtils.isEmpty(obtainedItems)).isFalse();
        assertThat(obtainedItems.size()).isEqualTo(items.size());
    }

    @Test
    @DisplayName("Test get available items by description")
    public void givenTwoItems_whenGetAvailableItemsByDescription_thenReturnTwoItems() {
        //given
        List<Item> items = List.of(ItemUtil.getItem1_WhereOwnerUser1(), ItemUtil.getItem2_WhereOwnerUser2());
        Mockito.when(itemRepository.findByNameOrDescription(anyString(), anyBoolean())).thenReturn(items);

        //when
        List<ItemDto> itemDtos = itemService.getAvailableItemsByName("dfsdf");

        //then
        assertThat(CollectionUtils.isEmpty(itemDtos)).isFalse();
        assertThat(items.size()).isEqualTo(itemDtos.size());
    }

    @Test
    @DisplayName("Test update item functionality")
    public void givenItemUpdateDto_whenUpdateItem_whenReturnUpdatedItem() {
        //given
        User user1 = UserUtil.getUser1();

        Item item = Item.builder()
                .id(1)
                .owner(user1)
                .available(true)
                .name("GIDRA")
                .description("SMOKE WEED EVERY DAY")
                .build();

        ItemUpdateDto itemUpdateDto = ItemUpdateDto.builder()
                .id(1)
                .owner(1)
                .available(true)
                .name("PONIKA")
                .description("не курю")
                .build();

        Item updatedItem = Item.builder()
                .id(1)
                .owner(user1)
                .available(true)
                .name("PONIKA")
                .description("не курю")
                .build();

        ItemDto itemDto = ItemDto.builder()
                .id(1)
                .owner(user1.getId())
                .available(true)
                .name("PONIKA")
                .description("не курю")
                .build();

        Mockito.when(itemRepository.findByIdAndOwnerId(anyInt(), anyInt())).thenReturn(Optional.of(item));
        Mockito.when(itemRepository.save(any(Item.class))).thenReturn(updatedItem);

        //when
        ItemDto obtainedItem = itemService.updateItem(itemUpdateDto);

        //then
        assertThat(obtainedItem).isNotNull();
        assertThat(obtainedItem).isEqualTo(itemDto);
    }

    @Test
    @DisplayName("Test update exist item functionality")
    public void givenItemUpdateDto_whenUpdateItem_whenExceptionIsThrow() {
        //given
        ItemUpdateDto itemUpdateDto = ItemUpdateDto.builder()
                .id(1)
                .owner(1)
                .available(true)
                .name("PONIKA")
                .description("не курю")
                .build();

        Mockito.when(itemRepository.findByIdAndOwnerId(anyInt(), anyInt())).thenReturn(Optional.empty());

        //when
        NotFoundEntityException exception = assertThrows(NotFoundEntityException.class,
                () -> itemService.updateItem(itemUpdateDto));

        //then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("User with id = %s " +
                "don't have item with = %s", itemUpdateDto.getOwner(), itemUpdateDto.getId());
    }

    @Test
    @DisplayName("Test post functionality")
    public void givenCommentCreateDto_whenPostComment_thenReturnCommentDto() {
        //given
        LocalDateTime time = LocalDateTime.now();

        Item item = ItemUtil.getItem2_WhereOwnerUser2();

        CommentCreateDto commentCreateDto = CommentCreateDto.builder()
                .itemId(1)
                .authorId(1)
                .created(LocalDateTime.now())
                .text("POPOPOPOPO")
                .created(time)
                .build();

        Comment comment = Comment.builder()
                .id(1)
                .text("POPOPOPOPO")
                .item(item)
                .author(UserUtil.getUser1())
                .created(time)
                .build();

        CommentDto commentDto = CommentDto.builder()
                .id(1)
                .created(time)
                .text("POPOPOPOPO")
                .authorName(UserUtil.getUser1().getName())
                .build();

        Mockito.when(bookingRepository.findByBookerIdAndItemIdAndStatusAndStartLessThan(anyInt(),
                        anyInt(),
                        any(Status.class),
                        any(LocalDateTime.class)))
                .thenReturn(List.of(Booking.builder().build()));

        Mockito.when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        Mockito.when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));

        //when
        CommentDto obtainedComment = itemService.postComment(commentCreateDto);

        //then
        assertThat(obtainedComment).isNotNull();
        assertThat(obtainedComment).isEqualTo(commentDto);
    }

    @Test
    @DisplayName("Test post unbooking functionality")
    public void givenCommentCreateDto_whenPostComment_thenExceptionIsThrow() {
        //given
        LocalDateTime time = LocalDateTime.now();

        Item item = ItemUtil.getItem2_WhereOwnerUser2();

        CommentCreateDto commentCreateDto = CommentCreateDto.builder()
                .itemId(1)
                .authorId(1)
                .created(LocalDateTime.now())
                .text("POPOPOPOPO")
                .created(time)
                .build();

        Mockito.when(bookingRepository.findByBookerIdAndItemIdAndStatusAndStartLessThan(anyInt(),
                        anyInt(),
                        any(Status.class),
                        any(LocalDateTime.class)))
                .thenReturn(List.of());

        Mockito.when(itemRepository.findById(anyInt())).thenReturn(Optional.of(item));

        //when
        CommentException exception = assertThrows(CommentException.class, () -> itemService.postComment(commentCreateDto));

        //then
        assertThat(exception).isNotNull();
        assertThat(exception.getMessage()).isEqualTo("Нельзя оставить комментарий когда вы не бронировали вещь");
    }
}

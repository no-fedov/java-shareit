package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import org.springframework.util.CollectionUtils;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingOwnerService;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemPresentDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.util.ItemUtil;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

@ExtendWith(MockitoExtension.class)
public class ItemOwnerServiceTests {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private BookingOwnerService bookingOwnerService;

    @InjectMocks
    private ItemOwnerServiceImp itemOwnerService;

    @Test
    @DisplayName("Test get owner's items ")
    public void givenUserId_whenGetUserItems_thenReturnItems() {
        //given
        int userID = 1;

        ItemDto itemDto = ItemDto.builder()
                .owner(1)
                .name("fdsf")
                .id(1)
                .description("sfaf")
                .available(true)
                .build();

        UserDto userDto = UserDto.builder()
                .name("fdsfsd")
                .email("sdfsdf@sdfsdf.ru")
                .build();

        BookingDto booking1 = BookingDto.builder()
                .item(itemDto)
                .booker(userDto)
                .start(LocalDateTime.of(2000, 1, 1, 1, 1))
                .end(LocalDateTime.of(2000, 1, 2, 1, 1))
                .build();

        BookingDto booking2 = BookingDto.builder()
                .item(itemDto)
                .booker(userDto)
                .start(LocalDateTime.of(2000, 1, 3, 1, 1))
                .end(LocalDateTime.of(2000, 1, 4, 1, 1))
                .build();


        Mockito.when(itemRepository.findByOwnerIdOrderByIdAsc(anyInt(), any(Pageable.class)))
                .thenReturn(List.of(ItemUtil.getItem1_WhereOwnerUser1()));

        Mockito.when(bookingOwnerService.findPreviousBooking(anyInt(), any(LocalDateTime.class)))
                .thenReturn(booking1);

        Mockito.when(bookingOwnerService.findNextBooking(anyInt(), any(LocalDateTime.class)))
                .thenReturn(booking2);

        Mockito.when(commentRepository.findCommentsByItemId(anyInt()))
                .thenReturn(List.of(CommentDto.builder().build()));

        //when
        List<ItemPresentDto> items = itemOwnerService.getUserItems(userID, Pageable.unpaged());

        //then
        assertThat(CollectionUtils.isEmpty(items)).isFalse();
        ItemPresentDto itemPresentDto = items.get(0);
        assertThat(itemPresentDto.getComments()).isNotNull();
        assertThat(itemPresentDto.getLastBooking()).isNotNull();
        assertThat(itemPresentDto.getNextBooking()).isNotNull();
    }
}

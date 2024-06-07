package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ItemController.class)
public class ItemControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    @Autowired
    private ObjectMapper objectMapper;

    private ItemCreateDto itemCreateDto;
    private ItemUpdateDto itemUpdateDto;
    private ItemDto itemDto;
    private ItemPresentDto itemPresentDto;
    private CommentCreateDto commentCreateDto;
    private CommentDto commentDto;

    @BeforeEach
    public void setup() {
        itemCreateDto = ItemCreateDto.builder()
                .name("Item 1")
                .owner(1)
                .description("Description 1")
                .available(true)
                .build();

        itemUpdateDto =  ItemUpdateDto.builder()
                .name("Updated Item")
                .owner(1)
                .description("Description 1")
                .available(true)
                .build();

        itemDto = ItemDto.builder()
                .id(1)
                .requestId(1)
                .available(true)
                .owner(1)
                .name("Item 1")
                .build();

        itemPresentDto = ItemPresentDto.builder()
                .id(1)
                .name("Name1")
                .available(true)
                .description("Description")
                .comments(List.of())
                .build();


        commentCreateDto = CommentCreateDto.builder()
                .authorId(2)
                .created(LocalDateTime.of(2222, 2,2,2,2))
                .text("GREAT ITEM")
                .itemId(1)
                .build();

        commentDto = CommentDto.builder().build();
    }

    @Test
    public void testAddItem() throws Exception {
        Mockito.when(itemService.addItem(any(ItemCreateDto.class))).thenReturn(itemDto);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemCreateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(itemDto)));

        Mockito.verify(itemService, Mockito.times(1)).addItem(any(ItemCreateDto.class));
    }

    @Test
    public void testUpdateItem() throws Exception {
        Mockito.when(itemService.updateItem(any(ItemUpdateDto.class))).thenReturn(itemDto);

        mockMvc.perform(patch("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(itemUpdateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(itemDto)));

        Mockito.verify(itemService, Mockito.times(1)).updateItem(any(ItemUpdateDto.class));
    }

    @Test
    public void testGetItemById() throws Exception {
        Mockito.when(itemService.findItem(anyInt(), anyInt())).thenReturn(itemPresentDto);

        mockMvc.perform(get("/items/{itemId}", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(itemPresentDto)));

        Mockito.verify(itemService, Mockito.times(1)).findItem(anyInt(), anyInt());
    }

    @Test
    public void testGetUserItems() throws Exception {
        List<ItemPresentDto> userItems = List.of(itemPresentDto);
        Mockito.when(itemService.getUserItems(anyInt(), any(Pageable.class))).thenReturn(userItems);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(userItems)));

        Mockito.verify(itemService, Mockito.times(1)).getUserItems(anyInt(), any(Pageable.class));
    }

    @Test
    public void testGetAvailableItemsByName() throws Exception {
        List<ItemDto> availableItems = List.of(itemDto);
        Mockito.when(itemService.getAvailableItemsByName(anyString(), any(Pageable.class))).thenReturn(availableItems);

        mockMvc.perform(get("/items/search")
                        .param("text", "item")
                        .param("from", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(availableItems)));

        Mockito.verify(itemService, Mockito.times(1)).getAvailableItemsByName(anyString(), any(Pageable.class));
    }

    @Test
    public void testPostComment() throws Exception {
        Mockito.when(itemService.postComment(any(CommentCreateDto.class))).thenReturn(commentDto);

        mockMvc.perform(post("/items/{itemId}/comment", 1)
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentCreateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(commentDto)));

        Mockito.verify(itemService, Mockito.times(1)).postComment(any(CommentCreateDto.class));
    }
}


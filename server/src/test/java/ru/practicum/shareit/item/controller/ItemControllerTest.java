package ru.practicum.shareit.item.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDtoForItemDto;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoWithBooking;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemService itemService;

    @Autowired
    private MockMvc mvc;

    @Test
    void createItem() throws Exception {
        Long userId = 5L;
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Вещь")
                .description("Описание вещи")
                .available(true)
                .build();

        when(itemService.createItem(itemDto, userId))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())));

        Mockito.verify(itemService, Mockito.times(1))
                .createItem(itemDto, userId);
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    void updateItem_updateName() throws Exception {
        Long userId = 5L;
        Long itemId = 1L;
        ItemDto itemDto = ItemDto.builder()
                .name("Вещь Обновленная")
                .build();

        ItemDto resultItemDto = ItemDto.builder()
                .id(1L)
                .name("Вещь Обновленная")
                .description("Описание вещи")
                .available(true)
                .build();

        when(itemService.updateItem(itemDto, itemId, userId))
                .thenReturn(resultItemDto);

        mvc.perform(patch("/items/" + itemId)
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(resultItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(resultItemDto.getName())))
                .andExpect(jsonPath("$.description", is(resultItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(resultItemDto.getAvailable())));

        Mockito.verify(itemService, Mockito.times(1))
                .updateItem(itemDto, itemId, userId);
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    void updateItem_updateDescription() throws Exception {
        Long userId = 5L;
        Long itemId = 1L;
        ItemDto itemDto = ItemDto.builder()
                .description("Описание Обновленное")
                .build();

        ItemDto resultItemDto = ItemDto.builder()
                .id(1L)
                .name("Вещь")
                .description("Описание Обновленное")
                .available(true)
                .build();

        when(itemService.updateItem(itemDto, itemId, userId))
                .thenReturn(resultItemDto);

        mvc.perform(patch("/items/" + itemId)
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(resultItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(resultItemDto.getName())))
                .andExpect(jsonPath("$.description", is(resultItemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(resultItemDto.getAvailable())));

        Mockito.verify(itemService, Mockito.times(1))
                .updateItem(itemDto, itemId, userId);
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    void getById() throws Exception {
        long userId = 5L;
        long bookerId = 3L;
        long itemId = 1L;
        LocalDateTime firstCommentDate = LocalDateTime.now().minusDays(20).truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime secondCommentDate = LocalDateTime.now().minusDays(10).truncatedTo(ChronoUnit.SECONDS);

        List<CommentDtoResponse> comments = List.of(
                CommentDtoResponse.builder()
                        .id(1L)
                        .text("Комментарий 1")
                        .authorName("Имя автора 1")
                        .created(firstCommentDate)
                        .build(),
                CommentDtoResponse.builder()
                        .id(2L)
                        .text("Комментарий 2")
                        .authorName("Имя автора 2")
                        .created(secondCommentDate)
                        .build());

        LocalDateTime lastBookingStartDate = LocalDateTime.now().minusDays(30);
        LocalDateTime lastBookingEndDate = LocalDateTime.now().minusDays(25);

        BookingDtoForItemDto lastBooking = BookingDtoForItemDto.builder()
                .id(1L)
                .start(lastBookingStartDate)
                .end(lastBookingEndDate)
                .itemId(itemId)
                .bookerId(bookerId)
                .build();

        ItemDtoWithBooking itemDtoWithBooking = ItemDtoWithBooking.builder()
                .id(itemId)
                .name("Вещь")
                .description("Описание вещи")
                .available(true)
                .lastBooking(lastBooking)
                .comments(comments)
                .build();

        when(itemService.getById(itemId, userId))
                .thenReturn(itemDtoWithBooking);

        mvc.perform(get("/items/" + itemId)
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoWithBooking.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoWithBooking.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoWithBooking.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDtoWithBooking.getAvailable())))
                .andExpect(jsonPath("$.lastBooking.id", is(itemDtoWithBooking.getLastBooking().getId()), Long.class))
                .andExpect(jsonPath("$.lastBooking.start", is(dateTimeFormatter.format(lastBookingStartDate))))
                .andExpect(jsonPath("$.lastBooking.end", is(dateTimeFormatter.format(lastBookingEndDate))))
                .andExpect(jsonPath("$.lastBooking.itemId", is(itemId), Long.class))
                .andExpect(jsonPath("$.lastBooking.bookerId", is(bookerId), Long.class))
                .andExpect(jsonPath("$.comments", hasSize(comments.size())))
                .andExpect(jsonPath("$.comments[0].id", is(comments.get(0).getId()), Long.class))
                .andExpect(jsonPath("$.comments[0].text", is(comments.get(0).getText())))
                .andExpect(jsonPath("$.comments[0].authorName", is(comments.get(0).getAuthorName())))
                .andExpect(jsonPath("$.comments[0].created", is(dateTimeFormatter.format(firstCommentDate))))
                .andExpect(jsonPath("$.comments[1].id", is(comments.get(1).getId()), Long.class))
                .andExpect(jsonPath("$.comments[1].text", is(comments.get(1).getText())))
                .andExpect(jsonPath("$.comments[1].authorName", is(comments.get(1).getAuthorName())))
                .andExpect(jsonPath("$.comments[1].created", is(dateTimeFormatter.format(secondCommentDate))));

        Mockito.verify(itemService, Mockito.times(1))
                .getById(itemId, userId);
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    void getById_whenOwnerIsNotExists() throws Exception {
        long itemId = 1L;
        long userId = 55L;

        when(itemService.getById(itemId, userId))
                .thenThrow(new UserNotFoundException("Пользователь с идентификатором = " + userId + " не найден."));

        mvc.perform(get("/items/" + itemId)
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        Mockito.verify(itemService, Mockito.times(1))
                .getById(itemId, userId);
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    void getAllByOwnerId() throws Exception {
        long userId = 5L;
        long bookerId = 3L;
        long itemId = 1L;
        LocalDateTime firstCommentDate = LocalDateTime.now().minusDays(10).truncatedTo(ChronoUnit.SECONDS);
        LocalDateTime secondCommentDate = LocalDateTime.now().minusDays(5).truncatedTo(ChronoUnit.SECONDS);

        List<CommentDtoResponse> comments = List.of(
                CommentDtoResponse.builder()
                        .id(1L)
                        .text("Комментарий 1")
                        .authorName("Имя автора 1")
                        .created(firstCommentDate)
                        .build(),
                CommentDtoResponse.builder()
                        .id(2L)
                        .text("Комментарий 2")
                        .authorName("Имя автора 2")
                        .created(secondCommentDate)
                        .build());

        LocalDateTime lastBookingStartDate = LocalDateTime.now().minusDays(20);
        LocalDateTime lastBookingEndDate = LocalDateTime.now().minusDays(15);

        BookingDtoForItemDto lastBooking = BookingDtoForItemDto.builder()
                .id(1L)
                .start(lastBookingStartDate)
                .end(lastBookingEndDate)
                .itemId(itemId)
                .bookerId(bookerId)
                .build();

        List<ItemDtoWithBooking> itemDtoWithBookings = List.of(
                ItemDtoWithBooking.builder()
                        .id(itemId)
                        .name("Вещь 1")
                        .description("Описание вещи 1")
                        .available(true)
                        .lastBooking(lastBooking)
                        .comments(comments)
                        .build(),
                ItemDtoWithBooking.builder()
                        .id(itemId + 1)
                        .name("Вещь 2")
                        .description("Описание вещи 2")
                        .available(false)
                        .build());
        int from = 0;
        int size = 10;
        when(itemService.findAllItemsByOwnerId(userId, from, size))
                .thenReturn(itemDtoWithBookings);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(itemDtoWithBookings.size())))
                .andExpect(jsonPath("$.[0].id", is(itemDtoWithBookings.get(0).getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemDtoWithBookings.get(0).getName())))
                .andExpect(jsonPath("$.[0].description", is(itemDtoWithBookings.get(0).getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemDtoWithBookings.get(0).getAvailable())))
                .andExpect(jsonPath("$.[0].lastBooking.id", is(itemDtoWithBookings.get(0).getLastBooking().getId()), Long.class))
                .andExpect(jsonPath("$.[0].lastBooking.start", is(dateTimeFormatter.format(lastBookingStartDate))))
                .andExpect(jsonPath("$.[0].lastBooking.end", is(dateTimeFormatter.format(lastBookingEndDate))))
                .andExpect(jsonPath("$.[0].lastBooking.itemId", is(itemId), Long.class))
                .andExpect(jsonPath("$.[0].lastBooking.bookerId", is(bookerId), Long.class))
                .andExpect(jsonPath("$.[0].comments", hasSize(comments.size())))
                .andExpect(jsonPath("$.[0].comments[0].id", is(comments.get(0).getId()), Long.class))
                .andExpect(jsonPath("$.[0].comments[0].text", is(comments.get(0).getText())))
                .andExpect(jsonPath("$.[0].comments[0].authorName", is(comments.get(0).getAuthorName())))
                .andExpect(jsonPath("$.[0].comments[0].created", is(dateTimeFormatter.format(firstCommentDate))))
                .andExpect(jsonPath("$.[0].comments[1].id", is(comments.get(1).getId()), Long.class))
                .andExpect(jsonPath("$.[0].comments[1].text", is(comments.get(1).getText())))
                .andExpect(jsonPath("$.[0].comments[1].authorName", is(comments.get(1).getAuthorName())))
                .andExpect(jsonPath("$.[0].comments[1].created", is(dateTimeFormatter.format(secondCommentDate))))
                .andExpect(jsonPath("$.[1].id", is(itemDtoWithBookings.get(1).getId()), Long.class))
                .andExpect(jsonPath("$.[1].name", is(itemDtoWithBookings.get(1).getName())))
                .andExpect(jsonPath("$.[1].description", is(itemDtoWithBookings.get(1).getDescription())))
                .andExpect(jsonPath("$.[1].available", is(itemDtoWithBookings.get(1).getAvailable())));

        Mockito.verify(itemService, Mockito.times(1))
                .findAllItemsByOwnerId(userId, from, size);
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    void searchByText() throws Exception {
        Long userId = 5L;
        List<ItemDto> itemsDto = List.of(
                ItemDto.builder()
                        .id(1L)
                        .name("Вещь 1")
                        .description("Описание вещи 1")
                        .available(true)
                        .build(),
                ItemDto.builder()
                        .id(2L)
                        .name("Вещь 2")
                        .description("Что-то на непонятном")
                        .available(true)
                        .build());

        String text = "Вещь";
        int from = 0;
        int size = 10;
        when(itemService.searchByText(text, from, size))
                .thenReturn(itemsDto);

        mvc.perform(get("/items/search")
                        .param("text", text)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(itemsDto.size())))
                .andExpect(jsonPath("$.[0].id", is(itemsDto.get(0).getId()), Long.class))
                .andExpect(jsonPath("$.[0].name", is(itemsDto.get(0).getName())))
                .andExpect(jsonPath("$.[0].description", is(itemsDto.get(0).getDescription())))
                .andExpect(jsonPath("$.[0].available", is(itemsDto.get(0).getAvailable())))
                .andExpect(jsonPath("$.[1].id", is(itemsDto.get(1).getId()), Long.class))
                .andExpect(jsonPath("$.[1].name", is(itemsDto.get(1).getName())))
                .andExpect(jsonPath("$.[1].description", is(itemsDto.get(1).getDescription())))
                .andExpect(jsonPath("$.[1].available", is(itemsDto.get(1).getAvailable())));

        Mockito.verify(itemService, Mockito.times(1))
                .searchByText(text, from, size);
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    void addComment() throws Exception {
        CommentDto commentDto = new CommentDto("Комментарий");
        LocalDateTime created = LocalDateTime.now().minusDays(10).truncatedTo(ChronoUnit.SECONDS);
        CommentDtoResponse commentDtoResponse = CommentDtoResponse.builder()
                .id(7L)
                .text("Комментарий")
                .authorName("Имя автора")
                .created(created)
                .build();

        long itemId = 1L;
        long userId = 5L;

        when(itemService.addComment(commentDto, userId, itemId))
                .thenReturn(commentDtoResponse);

        mvc.perform(post("/items/" + itemId + "/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDtoResponse.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDtoResponse.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDtoResponse.getAuthorName())))
                .andExpect(jsonPath("$.created", is(dateTimeFormatter.format(created))));

        Mockito.verify(itemService, Mockito.times(1))
                .addComment(commentDto, userId, itemId);
        Mockito.verifyNoMoreInteractions(itemService);
    }
}
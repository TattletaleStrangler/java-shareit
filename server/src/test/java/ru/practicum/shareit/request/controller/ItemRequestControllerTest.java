package ru.practicum.shareit.request.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.request.dto.AddItemRequestDto;
import ru.practicum.shareit.request.dto.GetItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;

    @Test
    void createItemRequest_statusOk() throws Exception {
        AddItemRequestDto addItemRequestDto = new AddItemRequestDto("Описание запроса");
        long userId = 1;
        LocalDateTime created = LocalDateTime.now();

        GetItemRequestDto getItemRequestDto = GetItemRequestDto.builder()
                .id(1L)
                .description("Описание запроса")
                .created(created)
                .items(List.of())
                .build();

        when(itemRequestService.createItemRequest(addItemRequestDto, userId))
                .thenReturn(getItemRequestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(addItemRequestDto))
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(getItemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(getItemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created").value(dateTimeFormatter.format(created)))
                .andExpect(jsonPath("$.items", is(getItemRequestDto.getItems())));

        Mockito.verify(itemRequestService, Mockito.times(1))
                .createItemRequest(addItemRequestDto, userId);
        Mockito.verifyNoMoreInteractions(itemRequestService);
    }

    @Test
    void getItemRequestByRequester() throws Exception {
        LocalDateTime created1 = LocalDateTime.now();
        LocalDateTime created2 = created1.plusDays(1);
        Long userId = 1L;

        List<GetItemRequestDto> requestDtos = List.of(
                GetItemRequestDto.builder()
                        .id(1L)
                        .description("Запрос 1")
                        .created(created1)
                        .items(List.of())
                        .build(),
                GetItemRequestDto.builder()
                        .id(2L)
                        .description("Запрос 2")
                        .created(created2)
                        .items(List.of())
                        .build()
        );

        when(itemRequestService.findAllByRequesterId(userId))
                .thenReturn(requestDtos);

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(requestDtos.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestDtos.get(0).getDescription())))
                .andExpect(jsonPath("$[0].created").value(dateTimeFormatter.format(created1)))
                .andExpect(jsonPath("$[0].items", is(requestDtos.get(0).getItems())))
                .andExpect(jsonPath("$[1].id", is(requestDtos.get(1).getId()), Long.class))
                .andExpect(jsonPath("$[1].description", is(requestDtos.get(1).getDescription())))
                .andExpect(jsonPath("$[1].created").value(dateTimeFormatter.format(created2)))
                .andExpect(jsonPath("$[1].items", is(requestDtos.get(0).getItems())));

        Mockito.verify(itemRequestService, Mockito.times(1))
                .findAllByRequesterId(userId);
        Mockito.verifyNoMoreInteractions(itemRequestService);
    }

    @Test
    void getAllItemRequest() throws Exception {
        LocalDateTime created1 = LocalDateTime.now();
        LocalDateTime created2 = created1.plusDays(1);
        Long userId = 1L;
        int from = 0;
        int size = 10;

        List<GetItemRequestDto> requestDtos = List.of(
                GetItemRequestDto.builder()
                        .id(1L)
                        .description("Запрос 1")
                        .created(created1)
                        .items(List.of())
                        .build(),
                GetItemRequestDto.builder()
                        .id(2L)
                        .description("Запрос 2")
                        .created(created2)
                        .items(List.of())
                        .build()
        );

        when(itemRequestService.findAll(userId, from, size)).thenReturn(requestDtos);

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(requestDtos.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(requestDtos.get(0).getDescription())))
                .andExpect(jsonPath("$[0].created").value(dateTimeFormatter.format(created1)))
                .andExpect(jsonPath("$[0].items", is(requestDtos.get(0).getItems())))
                .andExpect(jsonPath("$[1].id", is(requestDtos.get(1).getId()), Long.class))
                .andExpect(jsonPath("$[1].description", is(requestDtos.get(1).getDescription())))
                .andExpect(jsonPath("$[1].created").value(dateTimeFormatter.format(created2)))
                .andExpect(jsonPath("$[1].items", is(requestDtos.get(0).getItems())));

        Mockito.verify(itemRequestService, Mockito.times(1)).findAll(userId, from, size);
        Mockito.verifyNoMoreInteractions(itemRequestService);
    }

    @Test
    void getItemRequest() throws Exception {
        Long requestId = 1L;
        Long userId = 1L;
        LocalDateTime created = LocalDateTime.now();

        GetItemRequestDto getItemRequestDto = GetItemRequestDto.builder()
                .id(userId)
                .description("Описание запроса")
                .created(created)
                .items(List.of())
                .build();

        when(itemRequestService.getById(requestId, userId))
                .thenReturn(getItemRequestDto);

        mvc.perform(get("/requests/" + requestId)
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(getItemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(getItemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created").value(dateTimeFormatter.format(created)))
                .andExpect(jsonPath("$.items", is(getItemRequestDto.getItems())));

        Mockito.verify(itemRequestService, Mockito.times(1))
                .getById(requestId, userId);
        Mockito.verifyNoMoreInteractions(itemRequestService);
    }

    @Test
    void getItemRequest_whenNotExistRequestId_thenStatus404() throws Exception {
        Long requestId = 99L;
        Long userId = 1L;
        LocalDateTime created = LocalDateTime.now();

        when(itemRequestService.getById(requestId, userId))
                .thenThrow(new ItemRequestNotFoundException("Запрос с идентификатором = " + requestId + " не найден."));

        mvc.perform(get("/requests/" + requestId)
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        Mockito.verify(itemRequestService, Mockito.times(1))
                .getById(requestId, userId);
        Mockito.verifyNoMoreInteractions(itemRequestService);
    }

    @Test
    void getItemRequest_whenNotExistRequesterId_thenStatus404() throws Exception {
        Long requestId = 1L;
        Long userId = 99L;
        LocalDateTime created = LocalDateTime.now();

        when(itemRequestService.getById(requestId, userId))
                .thenThrow(new UserNotFoundException("Пользователь с идентификатором = " + userId + " не найден."));

        mvc.perform(get("/requests/" + requestId)
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        Mockito.verify(itemRequestService, Mockito.times(1))
                .getById(requestId, userId);
        Mockito.verifyNoMoreInteractions(itemRequestService);
    }
}
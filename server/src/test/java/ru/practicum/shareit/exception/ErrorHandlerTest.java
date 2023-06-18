package ru.practicum.shareit.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.controller.UserController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {BookingController.class, ItemController.class, ItemRequestController.class,
        UserController.class})
class ErrorHandlerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @MockBean
    ItemService itemService;

    @MockBean
    UserService userService;

    @MockBean
    ItemRequestService itemRequestService;

    @Autowired
    private MockMvc mvc;

    @Test
    void handleNotFoundException() throws Exception {
        long nonExistentItemId = 99L;
        long userId = 1L;

        when(itemService.getById(nonExistentItemId, userId))
                .thenThrow(new BookingNotFoundException("Предмет с идентификатором = " + nonExistentItemId + " не найден."));

        mvc.perform(get("/items/" + nonExistentItemId)
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        Mockito.verify(itemService, Mockito.times(1))
                .getById(nonExistentItemId, userId);
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    void handleNotEnoughRightsException() throws Exception {
        long itemId = 5L;
        long userId = 1L;

        ItemDto itemDto = ItemDto.builder()
                .name("Вещь")
                .description("Описание Обновленное")
                .available(true)
                .build();

        when(itemService.updateItem(itemDto, itemId, userId))
                .thenThrow(new NotEnoughRightsException("Пользователь не может редактировать чужие предметы."));

        mvc.perform(patch("/items/" + itemId)
                        .content(mapper.writeValueAsString(itemDto))
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());

        Mockito.verify(itemService, Mockito.times(1))
                .updateItem(itemDto, itemId, userId);
        Mockito.verifyNoMoreInteractions(itemService);
    }

    @Test
    void constraint() throws Exception {
        Long userId = 1L;
        UserDto userDto = UserDto.builder()
                .name("Serj Tankian")
                .email("serjtankian@mail.com")
                .build();

        when(userService.updateUser(userDto, userId))
                .thenThrow(new DataIntegrityViolationException("Нарушено правило уникальности email адреса пользователя."));

        mvc.perform(patch("/users/" + userId)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());

        Mockito.verify(userService, Mockito.times(1))
                .updateUser(userDto, userId);
        Mockito.verifyNoMoreInteractions(userService);
    }

    @Test
    void handleUnexpectedException() throws Exception {
        when(itemRequestService.findAll(anyLong(), anyInt(), anyInt()))
                .thenThrow(new RuntimeException("что-то пошло не так."));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());

        Mockito.verify(itemRequestService, Mockito.times(1))
                .findAll(anyLong(), anyInt(), anyInt());
        Mockito.verifyNoMoreInteractions(userService);
    }
}
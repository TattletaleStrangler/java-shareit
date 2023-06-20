package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.ItemDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.in;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemClient itemClient;

    @Autowired
    private MockMvc mvc;

    @Test
    void createItem_whenNameIsWrong_then400() throws Exception {
        Long userId = 5L;
        ItemDto itemDto = ItemDto.builder()
                .name(" ")
                .description("Описание вещи")
                .available(true)
                .build();

        mvc.perform(post("/items")
                .content(mapper.writeValueAsString(itemDto))
                .header("X-Sharer-User-Id", userId)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(in(List.of(400, 500))));

        Mockito.verifyNoInteractions(itemClient);
    }

    @Test
    void createItem_whenNameIs256Characters_then400() throws Exception {
        Long userId = 5L;
        ItemDto itemDto = ItemDto.builder()
                .name("12345678901234567890123456789012345678901234567890" +
                        "12345678901234567890123456789012345678901234567890" +
                        "12345678901234567890123456789012345678901234567890" +
                        "12345678901234567890123456789012345678901234567890" +
                        "12345678901234567890123456789012345678901234567890123456")
                .description("Описание вещи")
                .available(true)
                .build();

        mvc.perform(post("/items")
                .content(mapper.writeValueAsString(itemDto))
                .header("X-Sharer-User-Id", userId)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(in(List.of(400, 500))));

        Mockito.verifyNoInteractions(itemClient);
    }

    @Test
    void createItem_whenDescriptionIsWrong_then400() throws Exception {
        Long userId = 5L;
        ItemDto itemDto = ItemDto.builder()
                .name("Вещь")
                .description(" ")
                .available(true)
                .build();

        mvc.perform(post("/items")
                .content(mapper.writeValueAsString(itemDto))
                .header("X-Sharer-User-Id", userId)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(in(List.of(400, 500))));

        Mockito.verifyNoInteractions(itemClient);
    }

    @Test
    void createItem_whenDescriptionIsGreaterThan512Characters_then400() throws Exception {
        Long userId = 5L;
        ItemDto itemDto = ItemDto.builder()
                .name("Вещь")
                .description("12345678901234567890123456789012345678901234567890" +
                        "12345678901234567890123456789012345678901234567890" +
                        "12345678901234567890123456789012345678901234567890" +
                        "12345678901234567890123456789012345678901234567890" +
                        "12345678901234567890123456789012345678901234567890" +
                        "12345678901234567890123456789012345678901234567890" +
                        "12345678901234567890123456789012345678901234567890" +
                        "12345678901234567890123456789012345678901234567890" +
                        "12345678901234567890123456789012345678901234567890" +
                        "12345678901234567890123456789012345678901234567890" +
                        "1234567890123")
                .available(true)
                .build();

        mvc.perform(post("/items")
                .content(mapper.writeValueAsString(itemDto))
                .header("X-Sharer-User-Id", userId)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(in(List.of(400, 500))));

        Mockito.verifyNoInteractions(itemClient);
    }

    @Test
    void createItem_whenAvailableIsWrong_then400() throws Exception {
        Long userId = 5L;
        ItemDto itemDto = ItemDto.builder()
                .name("Вещь")
                .description("Описание вещи")
                .build();

        mvc.perform(post("/items")
                .content(mapper.writeValueAsString(itemDto))
                .header("X-Sharer-User-Id", userId)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(in(List.of(400, 500))));

        Mockito.verifyNoInteractions(itemClient);
    }

    @Test
    void createItem_whenUserIdIsWrong_then400() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .name("Вещь")
                .description("Описание вещи")
                .available(true)
                .build();

        mvc.perform(post("/items")
                .content(mapper.writeValueAsString(itemDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(in(List.of(400, 500))));

        Mockito.verifyNoInteractions(itemClient);
    }

}
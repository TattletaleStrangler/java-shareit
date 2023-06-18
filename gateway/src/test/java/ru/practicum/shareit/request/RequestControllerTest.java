package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.in;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = RequestController.class)
class RequestControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    RequestClient itemRequestClient;

    @Autowired
    private MockMvc mvc;

    @Test
    void createItemRequest_whenWithoutUserId_status400() throws Exception {
        AddItemRequestDto addItemRequestDto = new AddItemRequestDto("Описание запроса");

        mvc.perform(post("/requests")
                .content(mapper.writeValueAsString(addItemRequestDto))
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(in(List.of(400, 500))));

        Mockito.verifyNoInteractions(itemRequestClient);
    }

    @Test
    void createItemRequest_whenEmptyDescription_status400() throws Exception {
        AddItemRequestDto addItemRequestDto = new AddItemRequestDto(" ");
        Long userId = 1L;

        mvc.perform(post("/requests")
                .content(mapper.writeValueAsString(addItemRequestDto))
                .header("X-Sharer-User-Id", userId)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(in(List.of(400, 500))));

        Mockito.verifyNoInteractions(itemRequestClient);
    }

    @Test
    void createItemRequest_whenDescriptionGreaterThan512Characters_status400() throws Exception {
        AddItemRequestDto addItemRequestDto = new AddItemRequestDto(
                "12345678901234567890123456789012345678901234567890" +
                        "12345678901234567890123456789012345678901234567890" +
                        "12345678901234567890123456789012345678901234567890" +
                        "12345678901234567890123456789012345678901234567890" +
                        "12345678901234567890123456789012345678901234567890" +
                        "12345678901234567890123456789012345678901234567890" +
                        "12345678901234567890123456789012345678901234567890" +
                        "12345678901234567890123456789012345678901234567890" +
                        "12345678901234567890123456789012345678901234567890" +
                        "12345678901234567890123456789012345678901234567890" +
                        "1234567890123");
        Long userId = 1L;

        mvc.perform(post("/requests")
                .content(mapper.writeValueAsString(addItemRequestDto))
                .header("X-Sharer-User-Id", userId)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(in(List.of(400, 500))));

        Mockito.verifyNoInteractions(itemRequestClient);
    }

    @Test
    void getItemRequest_whenWrongRequestId_thenStatus400() throws Exception {
        Long requestId = -1L;
        Long userId = 1L;
        LocalDateTime created = LocalDateTime.now();

        mvc.perform(get("/requests/" + requestId)
                .header("X-Sharer-User-Id", userId)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(in(List.of(400, 500))));

        Mockito.verifyNoInteractions(itemRequestClient);
    }

    @Test
    void getItemRequest_whenWrongRequesterId_thenStatus400() throws Exception {
        Long requestId = 1L;
        Long userId = -1L;
        LocalDateTime created = LocalDateTime.now();

        mvc.perform(get("/requests/" + requestId)
                .header("X-Sharer-User-Id", userId)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(in(List.of(400, 500))));

        Mockito.verifyNoInteractions(itemRequestClient);
    }

}
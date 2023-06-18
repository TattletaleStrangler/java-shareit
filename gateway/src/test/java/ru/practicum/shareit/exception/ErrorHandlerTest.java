package ru.practicum.shareit.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.BookingClient;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.request.AddItemRequestDto;
import ru.practicum.shareit.request.RequestClient;
import ru.practicum.shareit.request.RequestController;
import ru.practicum.shareit.user.UserController;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {BookingController.class, RequestController.class})
class ErrorHandlerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    RequestClient requestClient;

    @MockBean
    BookingClient bookingClient;

    @Autowired
    private MockMvc mvc;


    @Test
    void handleBadRequestException() throws Exception {
        long userId = 1;
        AddItemRequestDto addItemRequestDto = new AddItemRequestDto("");

        mvc.perform(post("/requests")
                .content(mapper.writeValueAsString(addItemRequestDto))
                .header("X-Sharer-User-Id", userId)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(requestClient);
    }

    @Test
    void testHandleBadRequestException() throws Exception {
        long ownerId = 4L;
        String state = "Unsupported state";

        mvc.perform(get("/bookings/owner")
                .header("X-Sharer-User-Id", ownerId)
                .param("state", state)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(bookingClient);
    }
}
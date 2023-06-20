package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.AddBookingDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingClient bookingClient;

    @Autowired
    private MockMvc mvc;

    @Test
    void createBooking_whenStartDateIsWrong() throws Exception {
        long bookerId = 5L;
        long itemId = 1L;

        LocalDateTime bookingStartDate = null;
        LocalDateTime bookingEndDate = LocalDateTime.now().plusDays(10);
        AddBookingDto addBookingDto = new AddBookingDto(itemId, bookingStartDate, bookingEndDate);

        mvc.perform(post("/bookings")
                .content(mapper.writeValueAsString(addBookingDto))
                .header("X-Sharer-User-Id", bookerId)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(bookingClient);
    }

    @Test
    void createBooking_whenEndDateIsWrong() throws Exception {
        long bookerId = 5L;
        long itemId = 1L;

        LocalDateTime bookingStartDate = LocalDateTime.now().plusDays(10);
        LocalDateTime bookingEndDate = null;
        AddBookingDto addBookingDto = new AddBookingDto(itemId, bookingStartDate, bookingEndDate);

        mvc.perform(post("/bookings")
                .content(mapper.writeValueAsString(addBookingDto))
                .header("X-Sharer-User-Id", bookerId)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(bookingClient);
    }

    @Test
    void createBooking_whenEndDateBeforeStartDate_thenBadRequest() throws Exception {
        long bookerId = 5L;
        long itemId = 1L;

        LocalDateTime bookingStartDate = LocalDateTime.now().plusDays(10);
        LocalDateTime bookingEndDate = LocalDateTime.now().plusDays(5);
        AddBookingDto addBookingDto = new AddBookingDto(itemId, bookingStartDate, bookingEndDate);

        mvc.perform(post("/bookings")
                .content(mapper.writeValueAsString(addBookingDto))
                .header("X-Sharer-User-Id", bookerId)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(bookingClient);
    }

    @Test
    void createBooking_whenStartAndEndDateIsWrong() throws Exception {
        long bookerId = 5L;
        long itemId = 1L;

        LocalDateTime bookingStartDate = null;
        LocalDateTime bookingEndDate = null;
        AddBookingDto addBookingDto = new AddBookingDto(itemId, bookingStartDate, bookingEndDate);

        mvc.perform(post("/bookings")
                .content(mapper.writeValueAsString(addBookingDto))
                .header("X-Sharer-User-Id", bookerId)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(bookingClient);
    }

    @Test
    void findBookingsByOwnerId_whenWrongState() throws Exception {
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
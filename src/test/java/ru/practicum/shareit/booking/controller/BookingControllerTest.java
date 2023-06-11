package ru.practicum.shareit.booking.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.AddBookingDto;
import ru.practicum.shareit.booking.dto.GetBookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.BookingNotFoundException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    @Test
    void createBooking() throws Exception {
        long bookerId = 5L;
        long itemId = 1L;
        long bookingId = 1L;

        LocalDateTime bookingStartDate = LocalDateTime.parse("2023-07-23T17:40:50", dateTimeFormatter);
        LocalDateTime bookingEndDate = LocalDateTime.parse("2023-07-24T17:40:50", dateTimeFormatter);
        AddBookingDto addBookingDto = new AddBookingDto(bookingStartDate, bookingEndDate, itemId);

        GetBookingDto getBookingDto = GetBookingDto.builder()
                .id(bookingId)
                .start(bookingStartDate)
                .end(bookingEndDate)
                .booker(new GetBookingDto.Booker(bookerId))
                .item(new GetBookingDto.Item(itemId, "Название вещи"))
                .build();

        when(bookingService.createBooking(addBookingDto, bookerId))
                .thenReturn(getBookingDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(addBookingDto))
                        .header("X-Sharer-User-Id", bookerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(getBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(getBookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(getBookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.booker.id", is(getBookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(getBookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(getBookingDto.getItem().getName())));

        Mockito.verify(bookingService, Mockito.times(1))
                .createBooking(addBookingDto, bookerId);
        Mockito.verifyNoMoreInteractions(bookingService);
    }

    @Test
    void createBooking_whenDbIsWrong() throws Exception {
        long bookerId = 5L;
        long itemId = 1L;
        long bookingId = 1L;

        LocalDateTime bookingStartDate = LocalDateTime.parse("2023-07-23T17:40:50", dateTimeFormatter);
        LocalDateTime bookingEndDate = LocalDateTime.parse("2023-07-24T17:40:50", dateTimeFormatter);
        AddBookingDto addBookingDto = new AddBookingDto(bookingStartDate, bookingEndDate, itemId);

        GetBookingDto getBookingDto = GetBookingDto.builder()
                .id(bookingId)
                .start(bookingStartDate)
                .end(bookingEndDate)
                .booker(new GetBookingDto.Booker(bookerId))
                .item(new GetBookingDto.Item(itemId, "Название вещи"))
                .build();

        when(bookingService.createBooking(addBookingDto, bookerId))
                .thenThrow(new DataIntegrityViolationException("Ошибка записи в базу."));

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(addBookingDto))
                        .header("X-Sharer-User-Id", bookerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());

        Mockito.verify(bookingService, Mockito.times(1))
                .createBooking(addBookingDto, bookerId);
        Mockito.verifyNoMoreInteractions(bookingService);
    }

    @Test
    void createBooking_whenStartDateIsWrong() throws Exception {
        long bookerId = 5L;
        long itemId = 1L;

        LocalDateTime bookingStartDate = null;
        LocalDateTime bookingEndDate = LocalDateTime.now().plusDays(10);
        AddBookingDto addBookingDto = new AddBookingDto(bookingStartDate, bookingEndDate, itemId);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(addBookingDto))
                        .header("X-Sharer-User-Id", bookerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBooking_whenEndDateIsWrong() throws Exception {
        long bookerId = 5L;
        long itemId = 1L;

        LocalDateTime bookingStartDate = LocalDateTime.now().plusDays(10);
        LocalDateTime bookingEndDate = null;
        AddBookingDto addBookingDto = new AddBookingDto(bookingStartDate, bookingEndDate, itemId);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(addBookingDto))
                        .header("X-Sharer-User-Id", bookerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createBooking_whenStartAndEndDateIsWrong() throws Exception {
        long bookerId = 5L;
        long itemId = 1L;

        LocalDateTime bookingStartDate = null;
        LocalDateTime bookingEndDate = null;
        AddBookingDto addBookingDto = new AddBookingDto(bookingStartDate, bookingEndDate, itemId);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(addBookingDto))
                        .header("X-Sharer-User-Id", bookerId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void approveBooking() throws Exception {
        long bookingId = 1L;
        long ownerId = 5L;
        long itemId = 1L;
        boolean approved = true;
        LocalDateTime bookingStartDate = LocalDateTime.parse("2023-07-23T17:40:50", dateTimeFormatter);
        LocalDateTime bookingEndDate = LocalDateTime.parse("2023-07-24T17:40:50", dateTimeFormatter);

        GetBookingDto getBookingDto = GetBookingDto.builder()
                .id(bookingId)
                .start(bookingStartDate)
                .end(bookingEndDate)
                .booker(new GetBookingDto.Booker(ownerId))
                .item(new GetBookingDto.Item(itemId, "Название вещи"))
                .build();

        when(bookingService.approve(bookingId, ownerId, approved))
                .thenReturn(getBookingDto);

        mvc.perform(patch("/bookings/" + bookingId)
                        .header("X-Sharer-User-Id", ownerId)
                        .param("approved", String.valueOf(approved))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(getBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(getBookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(getBookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.booker.id", is(getBookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(getBookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(getBookingDto.getItem().getName())));

        Mockito.verify(bookingService, Mockito.times(1))
                .approve(bookingId, ownerId, approved);
        Mockito.verifyNoMoreInteractions(bookingService);
    }

    @Test
    void getBooking() throws Exception {
        long userId = 5L;
        long itemId = 1L;
        long bookingId = 1L;

        LocalDateTime bookingStartDate = LocalDateTime.parse("2023-07-23T17:40:50", dateTimeFormatter);
        LocalDateTime bookingEndDate = LocalDateTime.parse("2023-07-24T17:40:50", dateTimeFormatter);

        GetBookingDto getBookingDto = GetBookingDto.builder()
                .id(bookingId)
                .start(bookingStartDate)
                .end(bookingEndDate)
                .booker(new GetBookingDto.Booker(userId))
                .item(new GetBookingDto.Item(itemId, "Название вещи"))
                .build();

        when(bookingService.getById(bookingId, userId))
                .thenReturn(getBookingDto);

        mvc.perform(get("/bookings/" + bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(getBookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(getBookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(getBookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.booker.id", is(getBookingDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(getBookingDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(getBookingDto.getItem().getName())));

        Mockito.verify(bookingService, Mockito.times(1))
                .getById(bookingId, userId);
        Mockito.verifyNoMoreInteractions(bookingService);
    }

    @Test
    void getBooking_whenBookingDoesNotExist_shouldReturnStatus_404() throws Exception {
        long bookingId = 991L;
        long userId = 5L;

        when(bookingService.getById(bookingId, userId))
                .thenThrow(new BookingNotFoundException("Бронирование с идентификатором = " + bookingId + " не найдено."));

        mvc.perform(get("/bookings/" + bookingId)
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        Mockito.verify(bookingService, Mockito.times(1))
                .getById(bookingId, userId);
        Mockito.verifyNoMoreInteractions(bookingService);
    }

    @Test
    void findBookingsByBookerId() throws Exception {
        long userId = 5L;
        long itemId1 = 1L;
        long itemId2 = 2L;
        long bookingId1 = 1L;
        long bookingId2 = 2L;
        BookingState state = BookingState.FUTURE;
        int from = 0;
        int size = 10;

        LocalDateTime bookingStartDate1 = LocalDateTime.parse("2023-07-23T17:40:50", dateTimeFormatter);
        LocalDateTime bookingEndDate1 = LocalDateTime.parse("2023-07-24T17:40:50", dateTimeFormatter);
        LocalDateTime bookingStartDate2 = LocalDateTime.parse("2023-08-23T13:41:23", dateTimeFormatter);
        LocalDateTime bookingEndDate2 = LocalDateTime.parse("2023-08-24T15:41:32", dateTimeFormatter);

        List<GetBookingDto> bookings = List.of(
                GetBookingDto.builder()
                        .id(bookingId1)
                        .start(bookingStartDate1)
                        .end(bookingEndDate1)
                        .booker(new GetBookingDto.Booker(userId))
                        .item(new GetBookingDto.Item(itemId1, "Название вещи 1"))
                        .build(),
                GetBookingDto.builder()
                        .id(bookingId2)
                        .start(bookingStartDate2)
                        .end(bookingEndDate2)
                        .booker(new GetBookingDto.Booker(userId))
                        .item(new GetBookingDto.Item(itemId2, "Название вещи 2"))
                        .build()
        );

        when(bookingService.findBookingsByBookerId(userId, state, from, size))
                .thenReturn(bookings);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .param("state", String.valueOf(state))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(bookings.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookings.get(0).getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookings.get(0).getEnd().toString())))
                .andExpect(jsonPath("$[0].booker.id", is(bookings.get(0).getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(bookings.get(0).getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(bookings.get(0).getItem().getName())))
                .andExpect(jsonPath("$[1].id", is(bookings.get(1).getId()), Long.class))
                .andExpect(jsonPath("$[1].start", is(bookings.get(1).getStart().toString())))
                .andExpect(jsonPath("$[1].end", is(bookings.get(1).getEnd().toString())))
                .andExpect(jsonPath("$[1].booker.id", is(bookings.get(1).getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[1].item.id", is(bookings.get(1).getItem().getId()), Long.class))
                .andExpect(jsonPath("$[1].item.name", is(bookings.get(1).getItem().getName())));

        Mockito.verify(bookingService, Mockito.times(1))
                .findBookingsByBookerId(userId, state, from, size);
        Mockito.verifyNoMoreInteractions(bookingService);
    }

    @Test
    void findBookingsByOwnerId() throws Exception {
        long ownerId = 4L;
        long userId = 5L;
        long itemId1 = 1L;
        long itemId2 = 1L;
        long bookingId1 = 1L;
        long bookingId2 = 2L;
        BookingState state = BookingState.FUTURE;
        int from = 0;
        int size = 10;

        LocalDateTime bookingStartDate1 = LocalDateTime.parse("2023-07-23T17:40:50", dateTimeFormatter);
        LocalDateTime bookingEndDate1 = LocalDateTime.parse("2023-07-24T17:40:50", dateTimeFormatter);
        LocalDateTime bookingStartDate2 = LocalDateTime.parse("2023-08-23T13:41:23", dateTimeFormatter);
        LocalDateTime bookingEndDate2 = LocalDateTime.parse("2023-08-24T15:41:32", dateTimeFormatter);

        List<GetBookingDto> bookings = List.of(
                GetBookingDto.builder()
                        .id(bookingId1)
                        .start(bookingStartDate1)
                        .end(bookingEndDate1)
                        .booker(new GetBookingDto.Booker(userId))
                        .item(new GetBookingDto.Item(itemId1, "Название вещи 1"))
                        .build(),
                GetBookingDto.builder()
                        .id(bookingId2)
                        .start(bookingStartDate2)
                        .end(bookingEndDate2)
                        .booker(new GetBookingDto.Booker(userId))
                        .item(new GetBookingDto.Item(itemId2, "Название вещи 2"))
                        .build()
        );

        when(bookingService.findBookingsByOwnerId(ownerId, state, from, size))
                .thenReturn(bookings);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", ownerId)
                        .param("state", String.valueOf(state))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(bookings.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookings.get(0).getStart().toString())))
                .andExpect(jsonPath("$[0].end", is(bookings.get(0).getEnd().toString())))
                .andExpect(jsonPath("$[0].booker.id", is(bookings.get(0).getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.id", is(bookings.get(0).getItem().getId()), Long.class))
                .andExpect(jsonPath("$[0].item.name", is(bookings.get(0).getItem().getName())))
                .andExpect(jsonPath("$[1].id", is(bookings.get(1).getId()), Long.class))
                .andExpect(jsonPath("$[1].start", is(bookings.get(1).getStart().toString())))
                .andExpect(jsonPath("$[1].end", is(bookings.get(1).getEnd().toString())))
                .andExpect(jsonPath("$[1].booker.id", is(bookings.get(1).getBooker().getId()), Long.class))
                .andExpect(jsonPath("$[1].item.id", is(bookings.get(1).getItem().getId()), Long.class))
                .andExpect(jsonPath("$[1].item.name", is(bookings.get(1).getItem().getName())));

        Mockito.verify(bookingService, Mockito.times(1))
                .findBookingsByOwnerId(ownerId, state, from, size);
        Mockito.verifyNoMoreInteractions(bookingService);
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

        Mockito.verifyNoInteractions(bookingService);
    }
}
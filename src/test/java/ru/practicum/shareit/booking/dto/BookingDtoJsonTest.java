package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoJsonTest {

    @Autowired
    JacksonTester<BookingDtoForItemDto> json;

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Test
    void testSerialize() throws Exception {
        long bookingId = 1;
        long itemId = 1;
        long bookerId = 1;
        LocalDateTime start = LocalDateTime.parse("2023-06-06T10:00:00", dateTimeFormatter);
        LocalDateTime end = LocalDateTime.parse("2023-06-07T11:00:00", dateTimeFormatter);

        BookingDtoForItemDto dto = BookingDtoForItemDto.builder()
                .id(bookingId)
                .start(start)
                .end(end)
                .bookerId(bookerId)
                .itemId(itemId)
                .build();

        JsonContent<BookingDtoForItemDto> result = json.write(dto);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo((int) bookingId);
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(start.format(dateTimeFormatter));
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(end.format(dateTimeFormatter));
        assertThat(result).hasJsonPath("$.bookerId");
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo((int) bookerId);
        assertThat(result).hasJsonPath("$.itemId");
        assertThat(result).extractingJsonPathNumberValue("$.itemId").isEqualTo((int) itemId);
    }
}

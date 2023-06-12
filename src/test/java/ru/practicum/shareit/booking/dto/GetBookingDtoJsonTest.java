package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class GetBookingDtoJsonTest {
    @Autowired
    JacksonTester<GetBookingDto> json;

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Test
    void testSerialize() throws Exception {
        long bookingId = 1;
        long itemId = 1;
        long bookerId = 1;
        long ownerId = 2;
        BookingStatus status = BookingStatus.WAITING;

        UserDto ownerDto = UserDto.builder()
                .id(ownerId)
                .name("Daron Malakian")
                .email("daronmalakian@yandex.ru")
                .build();
        User owner = UserMapper.dtoToUser(ownerDto);

        UserDto bookerDto = UserDto.builder()
                .id(bookerId)
                .name("Serj Tankian")
                .email("serjtankian@yandex.ru")
                .build();
        User booker = UserMapper.dtoToUser(bookerDto);

        ItemDto itemDto = ItemDto.builder()
                .id(itemId)
                .name("Вещь 1")
                .description("Описание вещи 1")
                .available(true)
                .build();
        Item item = ItemMapper.dtoToItem(itemDto, owner, null);

        LocalDateTime start = LocalDateTime.parse("2023-06-06T10:00:00", dateTimeFormatter);
        LocalDateTime end = LocalDateTime.parse("2023-06-07T11:00:00", dateTimeFormatter);
        AddBookingDto addBookingDto = new AddBookingDto(start, end, itemId);
        Booking booking = BookingMapper.addBookingDtoToBooking(addBookingDto, booker, item, status);
        booking.setId(bookingId);

        GetBookingDto dto = BookingMapper.bookingToDto(booking);

        JsonContent<GetBookingDto> result = json.write(dto);
        assertThat(result).hasJsonPath("$.id");
        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo((int) bookingId);
        assertThat(result).hasJsonPath("$.start");
        assertThat(result).extractingJsonPathStringValue("$.start").isEqualTo(start.format(dateTimeFormatter));
        assertThat(result).hasJsonPath("$.end");
        assertThat(result).extractingJsonPathStringValue("$.end").isEqualTo(end.format(dateTimeFormatter));
        assertThat(result).hasJsonPath("$.booker");
        assertThat(result).hasJsonPath("$.booker.id");
        assertThat(result).extractingJsonPathNumberValue("$.booker.id").isEqualTo((int) bookerId);
        assertThat(result).hasJsonPath("$.item");
        assertThat(result).extractingJsonPathNumberValue("$.item.id").isEqualTo((int) itemId);
        assertThat(result).extractingJsonPathStringValue("$.item.name").isEqualTo(item.getName());
    }

}
package ru.practicum.shareit.booking.dto;

import lombok.*;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Booker booker;
    private Item item;
    private BookingStatus status;

    @AllArgsConstructor
    @Getter
    @Setter
    public static class Booker {
        private Long id;
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static class Item {
        private Long id;
        private String name;
    }

}

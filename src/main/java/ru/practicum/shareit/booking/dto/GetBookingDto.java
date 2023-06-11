package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class GetBookingDto {
    private Long id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime start;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
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

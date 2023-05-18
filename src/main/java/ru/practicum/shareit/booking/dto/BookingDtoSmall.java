package ru.practicum.shareit.booking.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * TODO Sprint add-bookings.
 */
@Data
public class BookingDtoSmall {
    private LocalDate start;
    private LocalDate end;
    private Long itemId;
}

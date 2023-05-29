package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.validator.StartBeforeEndDateValid;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@StartBeforeEndDateValid
public class BookingDtoSmall {
    @FutureOrPresent(message = "Дата начала бронирования не может быть в прошлом.")
    private LocalDateTime start;

    private LocalDateTime end;

    @NotNull
    private Long itemId;
}

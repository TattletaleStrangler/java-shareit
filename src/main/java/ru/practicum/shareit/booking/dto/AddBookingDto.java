package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.validator.StartBeforeEndDateValid;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@StartBeforeEndDateValid
public class AddBookingDto {
    @FutureOrPresent(message = "Дата начала бронирования не может быть в прошлом.")
    private LocalDateTime start;

    private LocalDateTime end;

    @NotNull
    private Long itemId;
}

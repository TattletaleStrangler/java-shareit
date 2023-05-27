package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.booking.validator.StartBeforeEndDateValid;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@StartBeforeEndDateValid
public class BookingDtoSmall {
    @NotNull(message = "Должны быть указаны дата начала и окончания бронирования.")
    @FutureOrPresent(message = "Дата начала бронирования не может быть в прошлом.")
    private LocalDateTime start;

    @NotNull(message = "Должны быть указаны дата начала и окончания бронирования.")
    @Future(message = "Дата завершения бронирования не может быть в прошлом.")
    private LocalDateTime end;

    @NotNull
    private Long itemId;
}

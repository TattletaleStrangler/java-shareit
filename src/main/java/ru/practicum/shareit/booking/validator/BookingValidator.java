package ru.practicum.shareit.booking.validator;

import ru.practicum.shareit.booking.dto.BookingDtoSmall;
import ru.practicum.shareit.exception.ValidationException;

import java.time.LocalDateTime;

public class BookingValidator {
    public static void bookingDtoValidation(BookingDtoSmall bookingDto) {
//        if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
//            throw new ValidationException("Должны быть указаны дата начала и окончания бронирования.");
//        }

        if (bookingDto.getStart().isAfter(bookingDto.getEnd()) || bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new ValidationException("Дата начала бронирования должна быть раньше даты окончания бронирования.");
        }
//
//        if (bookingDto.getStart().isBefore(LocalDateTime.now())) {
//            throw new ValidationException("Дата начала бронирования не может быть в прошлом.");
//        }
    }

}

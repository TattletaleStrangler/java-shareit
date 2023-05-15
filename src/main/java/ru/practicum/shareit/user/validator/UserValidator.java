package ru.practicum.shareit.user.validator;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

@Slf4j
public class UserValidator {
    public static void userDtoValidation(UserDto userDto) {
        nameValidation(userDto.getName());
        emailValidation(userDto.getEmail());
    }

    public static void nameValidation(String name) {
        if (name == null || name.isBlank()) {
            log.warn("Имя пользователя не может быть пустым.");
            throw new ValidationException("Имя пользователя не может быть пустым.");
        }
    }

    public static void emailValidation(String email) {
        if (email == null || email.isBlank() || !email.contains("@")) {
            log.warn("Введенное значение не является адресом электронной почты.");
            throw new ValidationException("Введенное значение не является адресом электронной почты.");
        }
    }

}

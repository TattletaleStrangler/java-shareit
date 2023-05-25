package ru.practicum.shareit.item.validator;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

@Slf4j
public class ItemValidator {
    public static void itemDtoValidation(ItemDto itemDto) {
        nameValidation(itemDto.getName());
        descriptionValidation(itemDto.getDescription());
        availableValidation(itemDto.getAvailable());
    }

    public static void nameValidation(String name) {
        if (name == null || name.isBlank()) {
            log.warn("Наименование предмета не может быть пустым.");
            throw new ValidationException("Наименование предмета не может быть пустым.");
        }
    }

    public static void descriptionValidation(String description) {
        if (description == null || description.isBlank()) {
            log.warn("Описание предмета не может быть пустым.");
            throw new ValidationException("Описание предмета не может быть пустым.");
        }
    }

    public static void availableValidation(Boolean available) {
        if (available == null) {
            log.warn("Статус предмета не может быть неопределенным.");
            throw new ValidationException("Статус предмета не может быть неопределенным.");
        }
    }

}

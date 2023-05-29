package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class ItemDto {

    private Long id;

    @NotBlank(message = "Наименование предмета не может быть пустым.")
    private String name;

    @NotBlank(message = "Описание предмета не может быть пустым.")
    private String description;

    @NotNull(message = "Статус предмета не может быть неопределенным.")
    private Boolean available;

}

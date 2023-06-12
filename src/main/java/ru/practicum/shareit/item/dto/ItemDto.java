package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
public class ItemDto {

    private Long id;

    @NotBlank(message = "Наименование предмета не может быть пустым.")
    @Size(max = 255, message = "Длина наименования предмета не должна превышать 255 символов.")
    private String name;

    @NotBlank(message = "Описание предмета не может быть пустым.")
    @Size(max = 512, message = "Длина описания предмета не должна превышать 512 символов.")
    private String description;

    @NotNull(message = "Статус предмета не может быть неопределенным.")
    private Boolean available;

    private Long requestId;
}

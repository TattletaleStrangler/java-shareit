package ru.practicum.shareit.request.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class AddItemRequestDto {
    @NotBlank(message = "Описание запроса не может быть пустым.")
    private String description;
}

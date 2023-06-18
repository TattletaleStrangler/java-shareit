package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AddItemRequestDto {
    @NotBlank(message = "Описание запроса не может быть пустым.")
    @Size(max = 512, message = "Текст запроса не должен превышать 512 символов.")
    private String description;
}

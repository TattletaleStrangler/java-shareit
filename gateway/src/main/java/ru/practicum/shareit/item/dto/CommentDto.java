package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    @NotBlank(message = "Текст комментария не может быть пустым.")
    @Size(min = 1, max = 512, message = "Текст комментария не должен превышать 512 символов.")
    private String text;

}

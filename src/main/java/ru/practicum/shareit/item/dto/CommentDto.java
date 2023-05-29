package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
public class CommentDto {
    private Long id;

    @NotBlank(message = "Текст комментария не может быть пустым.")
    @Size(min = 1, max = 512, message = "Текст комментария не должен превышать 512 символов.")
    private String text;

}

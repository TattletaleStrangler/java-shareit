package ru.practicum.shareit.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.validator.ValidateMarker;

import javax.validation.constraints.*;

@Data
@Builder
@AllArgsConstructor
public class UserDto {

    private Long id;

    @NotBlank(groups = ValidateMarker.Create.class, message = "Имя пользователя не может быть пустым.")
    @Size(max = 255, message = "Длина имени пользователя не должна превышать 255 символов.")
    private String name;

    @NotEmpty(groups = ValidateMarker.Create.class)
    @Email(groups = {ValidateMarker.Create.class, ValidateMarker.Update.class}, message = "Указанное значение email не является адресом электронной почты.")
    private String email;
}

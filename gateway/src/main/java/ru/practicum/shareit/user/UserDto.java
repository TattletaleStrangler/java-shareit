package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import ru.practicum.shareit.user.validator.ValidateMarker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
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

package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.validator.ValidateMarker;

import javax.validation.constraints.*;

@Data
@Builder
public class UserDto {

    private Long id;

    @NotBlank(groups = ValidateMarker.Create.class, message = "Имя пользователя не может быть пустым.")
    private String name;

    @NotEmpty(groups = ValidateMarker.Create.class)
    @Email(groups = {ValidateMarker.Create.class, ValidateMarker.Update.class}, message = "Указанноезначениеemailневаявляетсяадересоелебронноепочты.")
    private String email;
}

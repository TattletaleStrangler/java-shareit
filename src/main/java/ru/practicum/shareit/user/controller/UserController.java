package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.validator.ValidateMarker;

import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@Slf4j
@Validated
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserDto createUser(@Validated(ValidateMarker.Create.class) @RequestBody UserDto userDto) {
        log.info("Получен запрос POST /users");
        return userService.createUser(userDto);
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable @Min(1) Long userId) {
        log.info("Получен запрос GET /users/{}", userId);
        return userService.getById(userId);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@Validated(ValidateMarker.Update.class) @RequestBody UserDto userDto,
                              @PathVariable @Min(1) Long userId) {
        log.info("Получен запрос PATCH /users/{}", userId);
        return userService.updateUser(userDto, userId);
    }

    @GetMapping
    public List<UserDto> findAllUsers() {
        log.info("Получен запрос GET /users");
        return userService.findAllUsers();
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable @Min(1) long userId) {
        log.info("Получен запрос DELETE /users/{}", userId);
        userService.deleteUser(userId);
    }
}

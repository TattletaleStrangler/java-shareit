package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.validator.ValidateMarker;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @PostMapping
    @Validated(ValidateMarker.Create.class)
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        log.info("Получен запрос POST /users");
        return userService.createUser(userDto);
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable Long userId) {
        log.info("Получен запрос GET /users/{}", userId);
        return userService.getById(userId);
    }

    @PatchMapping("/{userId}")
    @Validated(ValidateMarker.Update.class)
    public UserDto updateUser(@Valid @RequestBody UserDto userDto, @PathVariable Long userId) {
        log.info("Получен запрос PATCH /users/{}", userId);
        return userService.updateUser(userDto, userId);
    }

    @GetMapping
    public List<UserDto> findAllUsers() {
        log.info("Получен запрос GET /users");
        return userService.findAllUsers();
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.info("Получен запрос DELETE /users/{}", userId);
        userService.deleteUser(userId);
    }
}

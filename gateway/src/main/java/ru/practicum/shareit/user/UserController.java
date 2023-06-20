package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.validator.ValidateMarker;

import javax.validation.constraints.Min;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> createUser(@Validated(ValidateMarker.Create.class) @RequestBody UserDto userDto) {
        log.info("POST /users");
        return userClient.createUser(userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> getById(@PathVariable @Min(1) Long userId) {
        log.info("GET /users/{}", userId);
        return userClient.getById(userId);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> updateUser(@Validated(ValidateMarker.Update.class) @RequestBody UserDto userDto,
                                             @PathVariable @Min(1) Long userId) {
        log.info("PATCH /users/{}", userId);
        return userClient.updateUser(userDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> findAllUsers() {
        log.info("GET /users");
        return userClient.findAllUsers();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> deleteUser(@PathVariable @Min(1) long userId) {
        log.info("DELETE /users/{}", userId);
        return userClient.deleteUser(userId);
    }
}

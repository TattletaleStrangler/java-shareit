package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto userDto);

    UserDto getById(long userId);

    UserDto updateUser(UserDto userDto, Long userId);

    List<UserDto> findAllUsers();

    void deleteUser(long id);

}

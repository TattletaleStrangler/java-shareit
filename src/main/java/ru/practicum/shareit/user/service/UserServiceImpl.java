package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.validator.UserValidator;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    @Override
    public UserDto createUser(UserDto userDto) {
        UserValidator.userDtoValidation(userDto);
        User user = UserMapper.DtoToUser(userDto);
        User savedUser = userDao.createUser(user);
        UserDto savedDto = UserMapper.userToDto(savedUser);
        return savedDto;
    }

    @Override
    public UserDto getById(long userId) {
        User user = userDao.getById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с идентификатором = " + userId + " не найден."));
        return UserMapper.userToDto(user);
    }

    @Override
    public UserDto updateUser(UserDto userDto, long userId) {
        User oldUser = userDao.getById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с идентификатором = " + userId + " не найден."));
        userDto.setId(userId);
        User newUser = UserMapper.DtoToUser(userDto);
        updateUser(newUser, oldUser);
        User updatedUser = userDao.updateUser(newUser);
        return UserMapper.userToDto(updatedUser);
    }

    @Override
    public List<UserDto> findAllUsers() {
        return userDao.findAllUsers().stream()
                .map(UserMapper::userToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(long id) {
        userDao.deleteUser(id);
    }

    private void updateUser(User newUser, User oldUser) {
        if (newUser.getName() == null) {
            newUser.setName(oldUser.getName());
        } else {
            UserValidator.nameValidation(newUser.getName());
        }

        if (newUser.getEmail() == null) {
            newUser.setEmail(oldUser.getEmail());
        } else {
            UserValidator.nameValidation(newUser.getEmail());
        }
    }

}

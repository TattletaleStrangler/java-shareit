package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.dtoToUser(userDto);
        User savedUser = userDao.save(user);
        UserDto savedDto = UserMapper.userToDto(savedUser);
        return savedDto;
    }

    @Override
    public UserDto getById(long userId) {
        User user = userDao.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с идентификатором = " + userId + " не найден."));
        return UserMapper.userToDto(user);
    }

    @Override
    public UserDto updateUser(UserDto userDto, Long userId) {
        User oldUser = userDao.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с идентификатором = " + userId + " не найден."));
        userDto.setId(userId);
        updateUser(userDto, oldUser);
        User updatedUser = userDao.save(oldUser);
        return UserMapper.userToDto(updatedUser);
    }

    @Override
    public List<UserDto> findAllUsers() {
        return userDao.findAll().stream()
                .map(UserMapper::userToDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUser(long id) {
        userDao.deleteById(id);
    }

    private void updateUser(UserDto newUser, User oldUser) {
        String name = newUser.getName();
        if (name != null && !name.isBlank()) {
            oldUser.setName(name);
        }

        String email = newUser.getEmail();
        if (email != null && !email.isBlank()) {
            oldUser.setEmail(email);
        }
    }

}

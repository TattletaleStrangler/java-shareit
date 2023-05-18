package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserAlreadyExistsException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.validator.UserValidator;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    @Override
    public UserDto createUser(UserDto userDto) {
        UserValidator.userDtoValidation(userDto);
        Optional<User> userWithSameEmail = userDao.findByEmail(userDto.getEmail());

        if (userWithSameEmail.isPresent()) {
            throw new UserAlreadyExistsException("Пользователь с email = " + userDto.getEmail() + " уже существует.");
        }

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
        User newUser = UserMapper.dtoToUser(userDto);
        updateUser(newUser, oldUser);
        User updatedUser = userDao.save(newUser);
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

    private void updateUser(User newUser, User oldUser) {
        if (newUser.getName() == null) {
            newUser.setName(oldUser.getName());
        } else {
            UserValidator.nameValidation(newUser.getName());
        }

        if (newUser.getEmail() == null) {
            newUser.setEmail(oldUser.getEmail());
        } else {
            UserValidator.emailValidation(newUser.getEmail());
            Optional<User> userWithSameEmail = userDao.findByEmail(newUser.getEmail());

            if (userWithSameEmail.isPresent() && !userWithSameEmail.get().getId().equals(newUser.getId())) {
                throw new UserAlreadyExistsException("Пользователь с email = " + newUser.getEmail() + " уже существует.");
            }
        }
    }

}

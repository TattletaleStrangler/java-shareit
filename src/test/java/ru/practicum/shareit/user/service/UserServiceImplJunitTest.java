package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RequiredArgsConstructor
@ExtendWith(MockitoExtension.class)
class UserServiceImplJunitTest {

    private static final long userId = 1L;
    private final UserDto userDto = UserDto.builder().id(userId).name("name").email("email").build();
    private final User user = UserMapper.dtoToUser(userDto);
    @Mock
    UserDao userDao;

    UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userDao);

    }

    @Test
    void getById() {
        when(userDao.findById(userId)).thenReturn(Optional.empty());

        final UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.getById(userId)
        );

        assertEquals("Пользователь с идентификатором = " + userId + " не найден.", exception.getMessage());

    }

    @Test
    void updateUser_whenUserNotFound_thenUserNotFoundException() {
        when(userDao.findById(userId)).thenReturn(Optional.empty());

        final UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> userService.updateUser(userDto, userId)
        );

        assertEquals("Пользователь с идентификатором = " + userId + " не найден.", exception.getMessage());
    }

    @Test
    void updateUser_whenNewNameIsNull() {
        when(userDao.findById(userId)).thenReturn(Optional.of(user));
        when(userDao.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto newUserDto = UserDto.builder().id(userId).name(null).email("new email").build();

        UserDto savedUserDto = userService.updateUser(newUserDto, userId);
        assertThat(savedUserDto.getId(), equalTo(user.getId()));
        assertThat(savedUserDto.getName(), equalTo(user.getName()));
        assertThat(savedUserDto.getEmail(), equalTo(newUserDto.getEmail()));
    }

    @Test
    void updateUser_whenNewNameIsBlank() {
        when(userDao.findById(userId)).thenReturn(Optional.of(user));
        when(userDao.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto newUserDto = UserDto.builder().id(userId).name("").email("new email").build();

        UserDto savedUserDto = userService.updateUser(newUserDto, userId);
        assertThat(savedUserDto.getId(), equalTo(user.getId()));
        assertThat(savedUserDto.getName(), equalTo(user.getName()));
        assertThat(savedUserDto.getEmail(), equalTo(newUserDto.getEmail()));
    }

    @Test
    void updateUser_whenNewEmailIsNull() {
        when(userDao.findById(userId)).thenReturn(Optional.of(user));
        when(userDao.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto newUserDto = UserDto.builder().id(userId).name("new name").email(null).build();

        UserDto savedUserDto = userService.updateUser(newUserDto, userId);
        assertThat(savedUserDto.getId(), equalTo(user.getId()));
        assertThat(savedUserDto.getName(), equalTo(newUserDto.getName()));
        assertThat(savedUserDto.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void updateUser_whenNewEmailIsBlank() {
        when(userDao.findById(userId)).thenReturn(Optional.of(user));
        when(userDao.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserDto newUserDto = UserDto.builder().id(userId).name("new name").email("").build();

        UserDto savedUserDto = userService.updateUser(newUserDto, userId);
        assertThat(savedUserDto.getId(), equalTo(user.getId()));
        assertThat(savedUserDto.getName(), equalTo(newUserDto.getName()));
        assertThat(savedUserDto.getEmail(), equalTo(user.getEmail()));
    }

}
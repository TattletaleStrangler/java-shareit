package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.model.User;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserDao userDao;

    public User createUser(User user) {
        return userDao.createUser(user);
    }

    public User getById(Long userId) {
        return userDao.getById(userId)
                .orElseThrow(() -> new UserNotFoundException("Пользователь с идентификатором = " + userId + " не найден."));
    }

    public User updateUser(User user) {
        return userDao.updateUser(user);
    }

    public List<User> findAllUsers() {
        return userDao.findAllUsers();
    }

    public void deleteUser(long id) {
        userDao.deleteUser(id);
    }

}

package ru.practicum.shareit.user.dao;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.UserAlreadyExistsException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDaoImplInMemory implements UserDao {

    private Long id = 1L;

    private Map<Long, User> users = new HashMap<>();

    @Override
    public User createUser(User user) {
        checkEmail(user);
        user.setId(getId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> getById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User updateUser(User user) {
        if (!users.containsKey(user.getId())) {
            throw new UserNotFoundException("Пользователь с идентификатором = " + user.getId() + " не найден.");
        }
        checkEmail(user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteUser(Long id) {
        users.remove(id);
    }

    private long getId() {
        return id++;
    }

    private void checkEmail(User user) {
        for (User u : users.values()) {
            if (u.getEmail().equalsIgnoreCase(user.getEmail()) && !u.getId().equals(user.getId())) {
                throw new UserAlreadyExistsException("Пользователь с email = " + user.getEmail() + " уже существует");
            }
        }
    }

}

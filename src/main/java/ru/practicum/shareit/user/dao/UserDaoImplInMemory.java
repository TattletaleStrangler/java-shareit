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

    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emailUniqueSet = new HashSet<>();

    @Override
    public User createUser(User user) {;
        if (emailUniqueSet.contains(user.getEmail())) {
            log.warn("Пользователь с email = {} уже существует.", user.getEmail());
            throw new UserAlreadyExistsException("Пользователь с email = " + user.getEmail() + " уже существует");
        }

        user.setId(getId());
        users.put(user.getId(), user);
        emailUniqueSet.add(user.getEmail());
        return user;
    }

    @Override
    public Optional<User> getById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User updateUser(User user) {
        User oldUser = users.get(user.getId());

        if (oldUser == null) {
            log.warn("Пользователь с идентификатором = {} не найден.", user.getId());
            throw new UserNotFoundException("Пользователь с идентификатором = " + user.getId() + " не найден.");
        }

        if (!oldUser.getEmail().equalsIgnoreCase(user.getEmail())) {
            if (emailUniqueSet.contains(user.getEmail())) {
                log.warn("Пользователь с email = {} уже существует.", user.getEmail());
                throw new UserAlreadyExistsException("Пользователь с email = " + user.getEmail() + " уже существует");
            }
            emailUniqueSet.remove(oldUser.getEmail());
        }

        emailUniqueSet.add(user.getEmail());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public void deleteUser(Long id) {
        User removedUser = users.remove(id);
        if (removedUser != null) {
            emailUniqueSet.remove(removedUser.getEmail());
        }
    }

    private long getId() {
        return id++;
    }

}

package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Optional;

public interface UserDao {

    User createUser(User user);

    Optional<User> getById(Long id);

    User updateUser(User user);

    List<User> findAllUsers();

    void deleteUser(Long id);

}

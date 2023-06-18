package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class UserServiceImplTest {

    private final EntityManager em;
    private final UserService userService;

    @Test
    void createUser() {
        UserDto userDto = UserDto.builder()
                .name("Serj Tankian")
                .email("serjtankian@mail.com")
                .build();

        userService.createUser(userDto);

        TypedQuery<User> query = em.createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", userDto.getEmail())
                .getSingleResult();

        assertThat(user, notNullValue());
        assertThat(user.getId(), equalTo(1L));
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void getById() {
        UserDto userDto = UserDto.builder()
                .name("Serj Tankian")
                .email("serjtankian@mail.com")
                .build();

        User entity = UserMapper.dtoToUser(userDto);
        em.persist(entity);
        em.flush();

        UserDto targetUser = userService.getById(entity.getId());

        assertThat(targetUser, notNullValue());
        assertThat(targetUser.getId(), equalTo(entity.getId()));
        assertThat(targetUser.getName(), equalTo(entity.getName()));
        assertThat(targetUser.getEmail(), equalTo(entity.getEmail()));

    }

    @Test
    void updateUser() {
        UserDto userDto = UserDto.builder()
                .name("Serj Tankian")
                .email("serjtankian@mail.com")
                .build();

        User user = UserMapper.dtoToUser(userDto);
        em.persist(user);
        em.flush();

        UserDto updateUserDto = UserDto.builder()
                .id(user.getId())
                .name("John Dolmayan")
                .email("johndolmayan@mail.com")
                .build();

        UserDto updatedUser = userService.updateUser(updateUserDto, updateUserDto.getId());

        assertThat(updatedUser, notNullValue());
        assertThat(updatedUser.getId(), equalTo(updateUserDto.getId()));
        assertThat(updatedUser.getName(), equalTo(updateUserDto.getName()));
        assertThat(updatedUser.getEmail(), equalTo(updateUserDto.getEmail()));
    }

    @Test
    void findAllUsers() {
        List<UserDto> sourceUsers = List.of(
                UserDto.builder()
                        .name("Serj Tankian")
                        .email("serjtankian@mail.com")
                        .build(),
                UserDto.builder()
                        .name("John Dolmayan")
                        .email("johndolmayan@mail.com")
                        .build()
        );

        for (UserDto user : sourceUsers) {
            User entity = UserMapper.dtoToUser(user);
            em.persist(entity);
        }
        em.flush();

        List<UserDto> targetUsers = userService.findAllUsers();

        assertThat(targetUsers, hasSize(sourceUsers.size()));
        for (UserDto sourceUser : sourceUsers) {
            assertThat(targetUsers, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(sourceUser.getName())),
                    hasProperty("email", equalTo(sourceUser.getEmail()))
            )));
        }
    }

    @Test
    void deleteUser() {
        UserDto userDto = UserDto.builder()
                .name("Serj Tankian")
                .email("serjtankian@mail.com")
                .build();

        User entity = UserMapper.dtoToUser(userDto);
        em.persist(entity);
        em.flush();

        userService.deleteUser(entity.getId());

        TypedQuery<User> query = em.createQuery("Select u from User u where u.id = :id", User.class);

        final NoResultException exception = assertThrows(
                NoResultException.class,
                () -> query.setParameter("id", entity.getId())
                        .getSingleResult()
        );

        assertEquals("No entity found for query", exception.getMessage());
    }
}
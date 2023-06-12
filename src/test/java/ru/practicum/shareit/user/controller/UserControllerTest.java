package ru.practicum.shareit.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService userService;

    @Autowired
    private MockMvc mvc;

    @Test
    void whenSaveNewUser_thenStatusIsOk() throws Exception {
        UserDto userDto = UserDto.builder()
                .name("John")
                .email("john.doe@mail.com")
                .build();

        when(userService.createUser(userDto))
                .thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        Mockito.verify(userService, Mockito.times(1))
                .createUser(userDto);
        Mockito.verifyNoMoreInteractions(userService);
    }

    @Test
    void whenSaveUserWithBlankEmail_thenStatusIsBadRequest() throws Exception {
        UserDto userDto = UserDto.builder().name("Alfredo").email(" ").build();

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(userService);
    }

    @Test
    void whenSaveUserWithNullEmail_thenStatusIsBadRequest() throws Exception {
        UserDto userDto = UserDto.builder().name("Alfredo").build();

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is(in(List.of(400, 500))));

        Mockito.verifyNoInteractions(userService);
    }

    @Test
    void whenSaveUserWith75SymbolsEmail_thenStatusIsBadRequest() throws Exception {
        UserDto userDto = UserDto.builder()
                .name("Alfredo")
                .email("qwertyuiopqwertyuiopqwertyuiopqwertyuiopqwertyuiop" +
                        "123456789012345@gmail.com")
                .build();

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(userService);
    }

    @Test
    void whenSaveNewUserWith74SymbolsEmail_thenStatusIsOk() throws Exception {
        UserDto userDto = UserDto.builder()
                .name("Alfredo")
                .email("qwertyuiopqwertyuiopqwertyuiopqwertyuiopqwertyuiop" +
                        "12345678901234@gmail.com")
                .build();

        when(userService.createUser(userDto))
                .thenReturn(userDto);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        Mockito.verify(userService, Mockito.times(1))
                .createUser(userDto);
        Mockito.verifyNoMoreInteractions(userService);
    }

    @Test
    void whenSaveUserWithBlankName_thenStatusIsBadRequest() throws Exception {
        UserDto userDto = UserDto.builder().name(" ").email("Alfredo@mail.com").build();

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(userService);
    }

    @Test
    void whenSaveUserWithNullName_thenStatusIsBadRequest() throws Exception {
        UserDto userDto = UserDto.builder().email("Alfredo@mail.com").build();

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(userService);
    }

    @Test
    void getById() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("John")
                .email("john.doe@mail.com")
                .build();

        when(userService.getById(userDto.getId()))
                .thenReturn(userDto);

        mvc.perform(get("/users/" + userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));

        Mockito.verify(userService, Mockito.times(1))
                .getById(userDto.getId());
        Mockito.verifyNoMoreInteractions(userService);
    }

    @Test
    void whenGetByWrongId_thenStatusIsBadRequest() throws Exception {
        Long wrongId = -1L;

        mvc.perform(get("/users/" + wrongId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(userService);
    }

    @Test
    void whenGetByNonExistentId_thenStatusIsBadRequest() throws Exception {
        Long nonExistentId = 99L;

        when(userService.getById(nonExistentId))
                .thenThrow(new UserNotFoundException("Пользователь с идентификатором = " + nonExistentId + " не найден."));

        mvc.perform(get("/users/" + nonExistentId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        Mockito.verify(userService, Mockito.times(1)).getById(nonExistentId);
        Mockito.verifyNoMoreInteractions(userService);
    }

    @Test
    void updateUser() throws Exception {
        Long userId = 1L;
        UserDto userDto = UserDto.builder()
                .name("Serj Tankian")
                .email("serjtankian@mail.com")
                .build();

        UserDto userDtoNew = UserDto.builder()
                .id(userId)
                .name("John Dolmayan")
                .email("johndolmayan@mail.com")
                .build();

        when(userService.updateUser(userDto, userId))
                .thenReturn(userDtoNew);

        mvc.perform(patch("/users/" + userId)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDtoNew.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDtoNew.getName())))
                .andExpect(jsonPath("$.email", is(userDtoNew.getEmail())));

        Mockito.verify(userService, Mockito.times(1))
                .updateUser(userDto, userId);
        Mockito.verifyNoMoreInteractions(userService);
    }

    @Test
    void whenUpdateByWrongId_thenStatusIsBadRequest() throws Exception {
        Long wrongId = -1L;
        UserDto userDto = UserDto.builder()
                .name("John")
                .email("john.doe@mail.com")
                .build();

        mvc.perform(patch("/users/" + wrongId)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(userService);
    }

    @Test
    void whenUpdateByNonExistentId_thenStatusIsBadRequest() throws Exception {
        Long nonExistentId = 99L;
        UserDto userDto = UserDto.builder()
                .name("John")
                .email("john.doe@mail.com")
                .build();

        when(userService.updateUser(userDto, nonExistentId))
                .thenThrow(new UserNotFoundException("Пользователь с идентификатором = " + nonExistentId + " не найден."));

        mvc.perform(patch("/users/" + nonExistentId)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", is("Пользователь с идентификатором = " + nonExistentId + " не найден.")));

        Mockito.verify(userService, Mockito.times(1)).updateUser(userDto, nonExistentId);
        Mockito.verifyNoMoreInteractions(userService);
    }

    @Test
    void whenUpdateUserWithNotEmail_thenStatusIsBadRequest() throws Exception {
        UserDto userDto = UserDto.builder()
                .name("Alfredo")
                .email("qwertyui@")
                .build();

        long anyId = 1L;
        mvc.perform(patch("/users/" + anyId)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(userService);
    }

    @Test
    void whenUpdateUserWith75SymbolsEmail_thenStatusIsBadRequest() throws Exception {
        UserDto userDto = UserDto.builder()
                .name("Alfredo")
                .email("qwertyuiopqwertyuiopqwertyuiopqwertyuiopqwertyuiop" +
                        "123456789012345@gmail.com")
                .build();

        long anyId = 1L;
        mvc.perform(patch("/users/" + anyId)
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(userService);
    }

    @Test
    void findAllUsers() throws Exception {
        List<UserDto> userDtoList = List.of(
                UserDto.builder()
                        .id(1L)
                        .name("Serj Tankian")
                        .email("serjtankian@mail.com")
                        .build(),
                UserDto.builder()
                        .id(2L)
                        .name("John Dolmayan")
                        .email("johndolmayan@mail.com")
                        .build());

        when(userService.findAllUsers()).thenReturn(userDtoList);

        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(userDtoList.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(userDtoList.get(0).getName())))
                .andExpect(jsonPath("$[0].email", is(userDtoList.get(0).getEmail())))
                .andExpect(jsonPath("$[1].id", is(userDtoList.get(1).getId()), Long.class))
                .andExpect(jsonPath("$[1].name", is(userDtoList.get(1).getName())))
                .andExpect(jsonPath("$[1].email", is(userDtoList.get(1).getEmail())))
                .andReturn();

        Mockito.verify(userService, Mockito.times(1)).findAllUsers();
        Mockito.verifyNoMoreInteractions(userService);
    }

    @Test
    void deleteUser() throws Exception {
        Long userId = 1L;

        mvc.perform(delete("/users/" + userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(userService, Mockito.times(1))
                .deleteUser(userId);
        Mockito.verifyNoMoreInteractions(userService);
    }
}
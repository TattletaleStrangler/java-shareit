package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.in;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserClient userClient;

    @Autowired
    MockMvc mvc;

    @Test
    void whenSaveUserWithBlankEmail_thenStatusIsBadRequest() throws Exception {
        UserDto userDto = UserDto.builder().name("Alfredo").email(" ").build();

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(userClient);
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

        Mockito.verifyNoInteractions(userClient);
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

        Mockito.verifyNoInteractions(userClient);
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

        Mockito.verifyNoInteractions(userClient);
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

        Mockito.verifyNoInteractions(userClient);
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

        Mockito.verifyNoInteractions(userClient);
    }

    @Test
    void whenGetByWrongId_thenStatusIsBadRequest() throws Exception {
        Long wrongId = -1L;

        mvc.perform(get("/users/" + wrongId)
                .characterEncoding(StandardCharsets.UTF_8)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Mockito.verifyNoInteractions(userClient);
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

        Mockito.verifyNoInteractions(userClient);
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

        Mockito.verifyNoInteractions(userClient);
    }

}
package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    private static final String BASE_PATH = "/users";
    private static final String JSON_PATH = "src/test/resources/request/userRequests/";

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testGetUsers() throws Exception {
        mockMvc.perform(get(BASE_PATH))
                .andExpect(status().isOk());
    }

    @Test
    void testCorrectPost() throws Exception {
        mockMvc.perform(post(BASE_PATH)
                .content(new String(Files.readAllBytes(Paths.get(JSON_PATH + "correct.json"))))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testNullLogin() throws Exception {
        mockMvc.perform(post(BASE_PATH)
                .content(new String(Files.readAllBytes(Paths.get(JSON_PATH + "nullLogin.json"))))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(forwardedUrl("Неверный формат логина"));
    }

    @Test
    void testBlankLogin() throws Exception {
        mockMvc.perform(post(BASE_PATH)
                .content(new String(Files.readAllBytes(Paths.get(JSON_PATH + "blankLogin.json"))))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(forwardedUrl("Неверный формат логина"));
    }

    @Test
    void testLoginWishSpace() throws Exception {
        mockMvc.perform(post(BASE_PATH)
                .content(new String(Files.readAllBytes(Paths.get(JSON_PATH + "wishSpaceLogin.json"))))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(forwardedUrl("Неверный формат логина"));
    }

    @Test
    void testNullName() throws Exception {
        mockMvc.perform(post(BASE_PATH)
                .content(new String(Files.readAllBytes(Paths.get(JSON_PATH + "nullName.json"))))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Sasha"));
    }

    @Test
    void testBlankName() throws Exception {
        mockMvc.perform(post(BASE_PATH)
                        .content(new String(Files.readAllBytes(Paths.get(JSON_PATH + "blankName.json"))))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Sasha"));
    }

    @Test
    void testNullEmail() throws Exception {
        mockMvc.perform(post(BASE_PATH)
                .content(new String(Files.readAllBytes(Paths.get(JSON_PATH + "nullEmail.json"))))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(forwardedUrl("Неверный формат почты"));
    }

    @Test
    void testMissingAmpersandInEmail() throws Exception {
        mockMvc.perform(post(BASE_PATH)
                        .content(new String(Files.readAllBytes(Paths.get(JSON_PATH + "missingAmpersandEmail.json"))))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(forwardedUrl("Неверный формат почты"));
    }

    @Test
    void testNullBirthday() throws Exception {
        mockMvc.perform(post(BASE_PATH)
                .content(new String(Files.readAllBytes(Paths.get(JSON_PATH + "nullBirthday.json"))))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(forwardedUrl("Дата рождения не может быть пустой"));
    }

    @Test
    void testBirthdayAfterNow() throws Exception {
        mockMvc.perform(post(BASE_PATH)
                        .content(new String(Files.readAllBytes(Paths.get(JSON_PATH + "birthdayAfterNow.json"))))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(forwardedUrl("Неверно указана дата рождения"));
    }

    @Test
    void testBlankId() throws Exception {
        mockMvc.perform(put(BASE_PATH)
                        .content(new String(Files.readAllBytes(Paths.get(JSON_PATH + "blankId.json"))))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(forwardedUrl("Id должен быть указан"));
    }

    @Test
    void testNullId() throws Exception {
        mockMvc.perform(put(BASE_PATH)
                        .content(new String(Files.readAllBytes(Paths.get(JSON_PATH + "nullId.json"))))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(forwardedUrl("Id должен быть указан"));
    }

    @Test
    void testCorrectUpdate() throws Exception {
        mockMvc.perform(put(BASE_PATH)
                        .content(new String(Files.readAllBytes(Paths.get(JSON_PATH + "update.json"))))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Masha"))
                .andExpect(jsonPath("$.id").value("1"))
                .andExpect(jsonPath("$.login").value("Mustang"))
                .andExpect(jsonPath("$.email").value("Shilla@mail.ru"))
                .andExpect(jsonPath("$.birthday").value("2005-03-20"));
    }
}
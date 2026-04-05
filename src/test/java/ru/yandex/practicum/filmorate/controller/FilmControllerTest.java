package ru.yandex.practicum.filmorate.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class FilmControllerTest {
    private static final String BASE_PATH = "/films";
    private static final String JSON_POST_PATH = "src/test/resources/request/filmRequests/post/";
    private static final String JSON_PUT_PATH = "src/test/resources/request/filmRequests/put/";

    @Autowired
    private MockMvc mockMvc;


    @Test
    void testGetFilms() throws Exception {
        mockMvc.perform(get(BASE_PATH))
                .andExpect(status().isOk());
    }

    @Test
    void testCorrectPost() throws Exception {
        mockMvc.perform(post(BASE_PATH)
                .content(new String(Files.readAllBytes(Paths.get(JSON_POST_PATH + "correct.json"))))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testEmptyPost() throws Exception {
        mockMvc.perform(post(BASE_PATH)
                        .content(new String(Files.readAllBytes(Paths.get(JSON_POST_PATH + "empty.json"))))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(forwardedUrl("Пустое тело запроса"));
    }

    @Test
    void testIncorrectNamePost() throws Exception {
        mockMvc.perform(post(BASE_PATH)
                .content(new String(Files.readAllBytes(Paths.get(JSON_POST_PATH + "incorrectName.json"))))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(forwardedUrl("Название фильма не может быть пустым"));
    }

    @Test
    void testIncorrectDataPost() throws Exception {
        mockMvc.perform(post(BASE_PATH)
                 .content(new String(Files.readAllBytes(Paths.get(JSON_POST_PATH + "incorrectDate.json"))))
                 .contentType(MediaType.APPLICATION_JSON))
                .andExpect(forwardedUrl("Дата релиза не может быть раньше чем " + FilmService.MIN_RELEASE_DATE));
    }

    @Test
    void testIncorrectDescriptionPost() throws Exception {
        mockMvc.perform(post(BASE_PATH)
                .content(new String(Files.readAllBytes(Paths.get(JSON_POST_PATH + "incorrectDescription.json"))))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(forwardedUrl("Описание должно быть менее 200 символов"));
    }

    @Test
    void testIncorrectDurationPost() throws Exception {
        mockMvc.perform(post(BASE_PATH)
                .content(new String(Files.readAllBytes(Paths.get(JSON_POST_PATH + "incorrectDuration.json"))))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(forwardedUrl("Продолжительность фильма не может быть отрицательной"));
    }

    @Test
    void testCorrectPut() throws Exception {
        mockMvc.perform(put(BASE_PATH)
                .content(new String(Files.readAllBytes(Paths.get(JSON_PUT_PATH + "correct.json"))))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void testIncorrectDatePut() throws Exception {
        mockMvc.perform(put(BASE_PATH)
                .content(new String(Files.readAllBytes(Paths.get(JSON_PUT_PATH + "incorrectDate.json"))))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(forwardedUrl("Дата релиза не может быть раньше чем " + FilmService.MIN_RELEASE_DATE));
    }

    @Test
    void testIncorrectDurationPut() throws Exception {
        mockMvc.perform(post(BASE_PATH)
                        .content(new String(Files.readAllBytes(Paths.get(JSON_POST_PATH + "correct.json"))))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());


        mockMvc.perform(put(BASE_PATH)
                        .content(new String(Files.readAllBytes(Paths.get(JSON_PUT_PATH + "incorrectDuration.json"))))
                        .contentType(MediaType.APPLICATION_JSON))
                 .andExpect(forwardedUrl("Продолжительность фильма не может быть отрицательной"));
    }

    @Test
    void testIncorrectDescriptionPut() throws Exception {

        mockMvc.perform(put(BASE_PATH)
                        .content(new String(Files.readAllBytes(Paths.get(JSON_PUT_PATH + "incorrectDescription.json"))))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(forwardedUrl("Описание должно быть менее 200 символов"));
    }

    @Test
    void testIncorrectNamePut() throws Exception {
        mockMvc.perform(put(BASE_PATH)
                .content(new String(Files.readAllBytes(Paths.get(JSON_PUT_PATH + "incorrectName.json"))))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.name").value("Godzilla"));
    }
}

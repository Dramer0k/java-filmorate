package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.GenresFilm;
import ru.yandex.practicum.filmorate.storage.genresFilm.GenresFilmStorage;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenresFilmService {
    private final GenresFilmStorage genresFilmStorage;

    public void addGenresFilm(Film film) throws InternalServerException {
        log.info("Добавляем жанры в db: {}", film.getGenres());
        genresFilmStorage.addGenresFilm(film);
        log.info("Жанры в db успешно добавлены");
    }

    public List<Long> getGenres(Long filmId) {
        log.info("Достаем жанры из db");
        List<Long> result = genresFilmStorage.getGenres(filmId);
        log.info("Жанры из db: {}", result);
        return result;
    }
}

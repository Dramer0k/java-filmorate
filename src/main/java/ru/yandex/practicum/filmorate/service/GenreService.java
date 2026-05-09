package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreStorage genreStorage;

    public List<Genre> findAllGenres() {
        log.info("GET /genres");
        List<Genre> result = genreStorage.findAllGenres();
        log.info("Получены жанры: {}", result);
        return result;
    }

    public Genre getGenreById(Long id) {
        log.info("Get /genres/{}", id );
        Genre result = genreStorage.getGenreById(id);
        log.info("Жанр {}: {}", id, result);
        return result;
    }

    public Film addGenreName(Film film) {
        log.info("Добавить названия в жанры: {}", film.getGenres());
        Film result = genreStorage.addGenre(film);
        log.info("Названия успешно добавлены: {}", film.getGenres());
        return result;
    }
}

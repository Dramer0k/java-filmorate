package ru.yandex.practicum.filmorate.storage.genresFilm;

import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface GenresFilmStorage {
    void addGenresFilm(Film film) throws InternalServerException;

    List<Long> getGenres(Long filmId);
}

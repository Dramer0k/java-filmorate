package ru.yandex.practicum.filmorate.storage.genresFilm;

import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Set;

public interface GenresFilmStorage {
    void addGenresFilm(Film film) throws InternalServerException;

    List<Long> getGenres(Long filmId);
}

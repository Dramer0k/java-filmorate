package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {

    Collection<Film> getAllFilms();

    Film addFilm(Film film) throws ValidationException, InternalServerException;

    Film removeFilm(Long filmId);

    Film updateFilm(Film film) throws ValidationException, InternalServerException;

    void setLike(Long id, Long userId) throws InternalServerException;

    void removeLike(Long id, Long userId);

    List<Film> getFavoritesFilms(String count);

    Film getFilm(Long filmId);
}

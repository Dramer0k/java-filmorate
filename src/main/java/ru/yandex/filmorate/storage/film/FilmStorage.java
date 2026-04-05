package ru.yandex.filmorate.storage.film;

import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmStorage {

    Collection<Film> getAllFilms();

    Film addFilm(Film film) throws ValidationException;

    Film removeFilm(Long filmId);

    Film updateFilm(Film film) throws ValidationException;

    void setLike(String id, String userId);

    void removeLike(String id, String userId);

    List<Film> getFavoritesFilms(String count);
}

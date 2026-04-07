package ru.yandex.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.filmorate.storage.user.InMemoryUserStorage;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    InMemoryUserStorage inMemoryUserStorage;
    private long id = 1;


    @Override
    public Collection<Film> getAllFilms() {
        return films.values();

    }

    @Override
    public Film addFilm(Film film) throws ValidationException {
        film.setId(id);
        id++;
        films.put(film.getId(), film);

        log.info("Новый фильм добавлен {}", film);
        return film;
    }

    @Override
    public Film removeFilm(Long filmId) {
        Film oldFilm = films.get(filmId);
        films.remove(filmId);

        log.info("Фильм {} с id {} удален", oldFilm.getName(), filmId);

        return oldFilm;
    }

    @Override
    public Film updateFilm(Film film) throws ValidationException {
        films.put(film.getId(), film);
        log.info("В информацию о фильме {} внесены изменения!", film.getId());
        return film;
    }

    @Override
    public void setLike(String id, String userId) {
        Film film = films.get(Long.valueOf(id));
        film.setLikeCount(film.getLikeCount() + 1);
        log.info("Лайк поставлен!");
    }

    @Override
    public void removeLike(String id, String userId) {
        Film film = films.get(Long.valueOf(id));
        film.setLikeCount(film.getLikeCount() - 1);
        log.info("Лайк удален!");
    }

    @Override
    public List<Film> getFavoritesFilms(String count) {
        return films.values().stream()
                .sorted(Comparator.comparing(Film::getLikeCount).reversed())
                .limit(Integer.parseInt(count))
                .toList();
    }

    public boolean checkFilm(Long filmId) {
        return films.containsKey(filmId);
    }

    public Film getFilm(Long filmId) {
        return films.get(filmId);
    }
}

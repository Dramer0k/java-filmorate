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
    @Autowired
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
        Film oldFilm = films.get(film.getId());
        if (oldFilm == null) {
            throw new NotFoundException("Фильма с id " + film.getId() + " не существует!");
        }

        if (film.getName() != null && !film.getName().isBlank()) {
            log.debug("Меняем название фильма {} с {} на {}", film.getId(), oldFilm.getName(), film.getName());
            oldFilm.setName(film.getName());
            log.info("Название фильма {} изменено на {}!", oldFilm.getId(), oldFilm.getName());
        }
        if (film.getDescription() != null && !film.getDescription().isBlank()) {
            log.debug("Меняем описание фильма {} с {} на {}", film.getId(), oldFilm.getDescription(), film.getDescription());
            oldFilm.setDescription(film.getDescription());
            log.info("Описание фильма {} изменено на {}!", oldFilm.getId(), oldFilm.getDescription());
        }
        if (film.getReleaseDate() != null) {
            log.debug("Меняем дату релиза фильма {} с {} на {}", film.getId(), oldFilm.getReleaseDate(), film.getReleaseDate());
            oldFilm.setReleaseDate(film.getReleaseDate());
            log.info("Дата релиза фильма {} изменена на {}!", oldFilm.getId(), oldFilm.getReleaseDate());
        }
        if (film.getDuration() != null) {
            log.debug("Меняем длительность фильма {} с {} на {}", film.getId(), oldFilm.getDuration(), film.getDuration());
            oldFilm.setDuration(film.getDuration());
            log.info("Длительность фильма {} изменена на {}!", oldFilm.getId(), oldFilm.getDuration());
        }

        log.info("В информацию о фильме {} внесены изменения!", oldFilm.getId());
        return oldFilm;
    }

    @Override
    public void setLike(String id, String userId) {
        User user = inMemoryUserStorage.getById(Long.valueOf(userId));
        Film film = films.get(Long.valueOf(id));
        if (film == null) {
            throw new NotFoundException("Фильм с id " + id + " не найден!");
        }
        if (user.getFavoriteFilms().contains(film.getId())) {
            throw new ConditionsNotMetException("Лайк на этот фильм уже поставлен!");
        }
        user.getFavoriteFilms().add(film.getId());
        film.setLikeCount(film.getLikeCount() + 1);
        log.info("Лайк поставлен!");
    }

    @Override
    public void removeLike(String id, String userId) {
        User user = inMemoryUserStorage.getById(Long.valueOf(userId));
        Film film = films.get(Long.valueOf(id));
        if (film == null) {
            throw new NotFoundException("Фильм с id " + id + " не найден!");
        }
        if (!user.getFavoriteFilms().contains(film.getId())) {
            throw new ConditionsNotMetException("Лайка на этом фильме нет!");
        }
        user.getFavoriteFilms().remove(film.getId());
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

package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
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

        return film;
    }

    @Override
    public Film removeFilm(Long filmId) {
        Film oldFilm = films.get(filmId);
        films.remove(filmId);

        return oldFilm;
    }

    @Override
    public Film updateFilm(Film film) throws ValidationException {
        Film oldFilm = getFilm(film.getId());

        if (oldFilm == null) {
            throw new NotFoundException("Фильма с id " + film.getId() + " не существует!");
        }
        if (film.getName() == null || film.getName().isBlank()) {
            film.setName(oldFilm.getName());
        }
        if (film.getDescription() == null || film.getDescription().isBlank()) {
            film.setDescription(oldFilm.getDescription());
        }
        if (film.getReleaseDate() == null) {
            film.setReleaseDate(oldFilm.getReleaseDate());
        }
        if (film.getDuration() == null) {
            film.setDuration(oldFilm.getDuration());
        }

        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void setLike(Long id, Long userId) {
        Film film = films.get(id);
        film.setLikeCount(film.getLikeCount() + 1);
    }

    @Override
    public void removeLike(Long id, Long userId) {
        Film film = films.get(id);
        film.setLikeCount(film.getLikeCount() - 1);
    }

    @Override
    public List<Film> getFavoritesFilms(String count) {
        return films.values().stream()
                .sorted(Comparator.comparing(Film::getLikeCount).reversed())
                .limit(Integer.parseInt(count))
                .toList();
    }

    public Film getFilm(Long filmId) {
        return films.get(filmId);
    }
}

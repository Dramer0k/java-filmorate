package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
    public class FilmService {
    public final FilmStorage filmStorage;
    public static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    public static final int MAX_DESCRIPTION_LENGTH = 200;

    public Collection<Film> getAllFilms() {
        log.info("GET /films");
        return filmStorage.getAllFilms();
    }

    public Film addFilm(Film film) throws ValidationException {
        log.info("POST /films {}", film);

        checkValidateDate(film);
        checkValidateDescription(film);
        checkValidateDuration(film);
        checkValidateName(film);

        return filmStorage.addFilm(film);
    }

    public Film removeFilm(Long filmId) throws ValidationException {
        log.info("DELETE /films?fimlId={}", filmId);
        if (filmId == null) {
            throw new ValidationException("Id фильма не может быть пустым");
        }

        log.info("Удалить фильм с id {}", filmId);

        return filmStorage.removeFilm(filmId);
    }

    public Film updateFilm(Film film) throws ValidationException {
        log.info("PUT /films {}", film);

        if (film.getId() == null || film.getId().toString().isBlank()) {
            log.debug("Не указан id {}", film.getId());
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        checkValidateDescription(film);
        checkValidateDate(film);
        checkValidateDuration(film);

        return filmStorage.updateFilm(film);
    }

    public void checkValidateName(Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isBlank()) {
            log.debug("Название фильма пустое {}", film.getName());
            throw new ValidationException("Название фильма не может быть пустым");
        }
    }

    public void checkValidateDescription(Film film) throws ValidationException {
        if (film.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            log.debug("Описание фильма более 200 символов: {}", film.getDescription().length());
            throw new ValidationException("Описание должно быть менее 200 символов");
        }
    }

    public void checkValidateDate(Film film) throws ValidationException {
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.debug("Дата фильма меньше {}: {}", MIN_RELEASE_DATE, film.getReleaseDate());
            throw new ValidationException("Дата релиза не может быть раньше чем " + MIN_RELEASE_DATE);
        }
    }

    public void checkValidateDuration(Film film) throws ValidationException {
        if (film.getDuration() == null) {
            log.debug("Некорректно указана продолжительность фильма: {}", (Object) null);
            throw new ValidationException("Продолжительность фильма не может быть null");
        }
        if (film.getDuration() < 0) {
            log.debug("Некорректно указана продолжительность фильма: {}", film.getDuration());
            throw new ValidationException("Продолжительность фильма не может быть отрицательной");
        }
    }

    public void setLike(String id, String userId) {
        log.info("Поставить лайк");
        filmStorage.setLike(id, userId);
    }

    public void removeFilm(String id, String userId) {
        log.info("Убрать лайк");
        filmStorage.removeLike(id, userId);
    }

    public List<Film> getFavoritesFilms(String count) {
        log.info("Запрос топ {} фильмов", count);
        return filmStorage.getFavoritesFilms(count);
    }
}

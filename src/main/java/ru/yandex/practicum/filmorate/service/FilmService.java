package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.filmorate.storage.film.FilmStorage;
import ru.yandex.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
    public class FilmService {
    public final UserStorage userStorage;
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
        Film oldFilm = filmStorage.getFilm(film.getId());

        checkValidateDescription(film);
        checkValidateDate(film);
        checkValidateDuration(film);


        if (film.getId() == null || film.getId().toString().isBlank()) {
            log.debug("Не указан id {}", film.getId());
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (oldFilm == null) {
            throw new NotFoundException("Фильма с id " + film.getId() + " не существует!");
        }
        if (film.getName() != null || !film.getName().isBlank()) {
            log.debug("Меняем название фильма {} с {} на {}", film.getId(), oldFilm.getName(), film.getName());
            film.setName(oldFilm.getName());
            log.info("Название фильма {} изменено на {}!", oldFilm.getId(), oldFilm.getName());
        }
        if (film.getDescription() != null || !film.getDescription().isBlank()) {
            log.debug("Меняем описание фильма {} с {} на {}", film.getId(), oldFilm.getDescription(), film.getDescription());
            film.setDescription(oldFilm.getDescription());
            log.info("Описание фильма {} изменено на {}!", oldFilm.getId(), oldFilm.getDescription());
        }
        if (film.getReleaseDate() != null) {
            log.debug("Меняем дату релиза фильма {} с {} на {}", film.getId(), oldFilm.getReleaseDate(), film.getReleaseDate());
            film.setReleaseDate(oldFilm.getReleaseDate());
            log.info("Дата релиза фильма {} изменена на {}!", oldFilm.getId(), oldFilm.getReleaseDate());
        }
        if (film.getDuration() != null) {
            log.debug("Меняем длительность фильма {} с {} на {}", film.getId(), oldFilm.getDuration(), film.getDuration());
            oldFilm.setDuration(film.getDuration());
            film.setDuration(oldFilm.getDuration());
            log.info("Длительность фильма {} изменена на {}!", oldFilm.getId(), oldFilm.getDuration());
        }



        return filmStorage.updateFilm(film);
    }

    private void checkValidateName(Film film) throws ValidationException {
        if (film.getName() == null || film.getName().isBlank()) {
            log.debug("Название фильма пустое {}", film.getName());
            throw new ValidationException("Название фильма не может быть пустым");
        }
    }

    private void checkValidateDescription(Film film) throws ValidationException {
        if (film.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            log.debug("Описание фильма более 200 символов: {}", film.getDescription().length());
            throw new ValidationException("Описание должно быть менее 200 символов");
        }
    }

    private void checkValidateDate(Film film) throws ValidationException {
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.debug("Дата фильма меньше {}: {}", MIN_RELEASE_DATE, film.getReleaseDate());
            throw new ValidationException("Дата релиза не может быть раньше чем " + MIN_RELEASE_DATE);
        }
    }

    private void checkValidateDuration(Film film) throws ValidationException {
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
        if (filmStorage.getFilm(Long.valueOf(id)) == null) {
            throw new NotFoundException("Фильма с id " + id + " е существует");
        }
        filmStorage.setLike(id, userId);
        userStorage.setLike(id, userId);
    }

    public void removeFilm(String id, String userId) {
        log.info("Убрать лайк");
        if (filmStorage.getFilm(Long.valueOf(id)) == null) {
            throw new NotFoundException("Фильм с id " + id + " не найден!");
        }
        filmStorage.removeLike(id, userId);
        userStorage.removeLike(id, userId);
    }

    public List<Film> getFavoritesFilms(String count) {
        log.info("Запрос топ {} фильмов", count);
        return filmStorage.getFavoritesFilms(count);
    }
}

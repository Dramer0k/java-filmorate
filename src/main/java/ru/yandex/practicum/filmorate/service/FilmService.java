package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final GenreStorage genreStorage;
    private final MpaStorage mpaStorage;
    private final MpaService mpaService;
    private final GenreService genreService;
    private final GenresFilmService genresFilmService;
    public static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    public static final int MAX_DESCRIPTION_LENGTH = 200;

    public Collection<Film> getAllFilms() {
        log.info("GET /films");
        return filmStorage.getAllFilms();
    }

    public Film addFilm(Film film) throws ValidationException, InternalServerException {
        log.info("POST /films {}", film);

        checkValidateDate(film);
        checkValidateDescription(film);
        checkValidateDuration(film);
        checkValidateName(film);
        checkValidateMpa(film);
        checkValidateGenre(film);

        Film newFilm = genreService.addGenreName(film);
        newFilm = mpaService.addMpa(newFilm);
        newFilm = filmStorage.addFilm(newFilm);
        genresFilmService.addGenresFilm(newFilm);

        return newFilm;
    }

    public Film removeFilm(Long filmId) throws ValidationException {
        log.info("DELETE /films?fimlId={}", filmId);
        if (filmId == null) {
            throw new ValidationException("Id фильма не может быть пустым");
        }

        Film result = filmStorage.removeFilm(filmId);
        log.info("Фильм {} удален", filmId);

        return result;
    }

    public Film updateFilm(Film film) throws ValidationException, InternalServerException {
        log.info("PUT /films");
        log.info("Фильм донор: {}", film);

        checkValidateDescription(film);
        checkValidateDate(film);
        checkValidateDuration(film);
        if (film.getMpa() != null) {
            checkValidateMpa(film);
        }
        if (!film.getGenres().isEmpty()) {
            checkValidateGenre(film);
        }

        if (film.getId() == null || film.getId().toString().isBlank()) {
            log.debug("Не указан id {}", film.getId());
            throw new NotFoundException("Id должен быть указан");
        }
        log.info("Фильм {} до апдейта: {}", film.getId(), filmStorage.getFilm(film.getId()));
        Film result = filmStorage.updateFilm(film);
        log.info("Фильм {} после апдейта: {}", film.getId(), result);

        return result;
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

    private void checkValidateMpa(Film film) throws NotFoundException {
        if (film.getMpa().getId() > 5) {
            log.debug("Некорректно указан id map: {}", film.getMpa().getId());
            throw new NotFoundException("id map должен быть в диапазоне 1-5");
        }
    }

    private void checkValidateGenre(Film film) throws NotFoundException {
        List<Genre> genreList = film.getGenres();
        for (Genre genre : genreList) {
            if (genre.getId() > 6) {
                log.debug("Некорректно указан id genre: {}", film.getMpa().getId());
                throw new NotFoundException("id genre должен быть в диапазоне 1-6");
            }
        }
    }

    public void setLike(Long id, Long userId) throws InternalServerException {
        log.info("Поставить лайк");
        if (filmStorage.getFilm(id) == null) {
            throw new NotFoundException("Фильма с id " + id + " не существует");
        }
        if (userStorage.getUserById(id) == null) {
            throw new NotFoundException("Пользователя с id " + id + " не существует");
        }
        filmStorage.setLike(id, userId);
        userStorage.setLike(id, userId);
        log.info("Лайк успешно поставлен. Пользователь: {}, фильм: {}", userId, id);
    }

    public void removeFilm(Long id, Long userId) {
        log.info("Убрать лайк с фильма {}", id);
        if (filmStorage.getFilm(id) == null) {
            throw new NotFoundException("Фильм с id " + id + " не найден!");
        }
        filmStorage.removeLike(id, userId);
        userStorage.removeLike(id, userId);
        log.info("Лайк успешно удален. Пользователь: {}, фильм: {}", userId, id);
    }

    public List<Film> getFavoritesFilms(String count) {
        List<Film> result = filmStorage.getFavoritesFilms(count);
        log.info("Возвращаем список топ фильмов");
        log.info("Count: {}, список: {}", count, result);
        return result;
    }

    public Film getFilmById(Long id) {
        log.info("Запрос фильма: {}", id);
        Film film = filmStorage.getFilm(id);
        film.setGenres(genreStorage.getGenres(genresFilmService.getGenres(id)));
        film.setMpa(mpaStorage.findMpaById(film.getMpaId()));
        log.info("Фильм {}: {}", id, film);
        return film;
    }
}

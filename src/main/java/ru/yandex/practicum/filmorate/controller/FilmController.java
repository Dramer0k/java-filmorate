package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Long, Film> films = new HashMap<>();
    public static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    public final int MAX_DESCRIPTION_LENGTH = 200;
    private long id = 1;

    @GetMapping
    public Collection<Film> getFilms() {
        log.info("GET /films");
        return films.values();
    }

    @PostMapping
    public Film create(@RequestBody Film film) throws ValidationException {
        log.info("POST /films {}", film);

        checkValidateDate(film);
        checkValidateDescription(film);
        checkValidateDuration(film);
        checkValidateName(film);

        film.setId(id);
        id++;
        films.put(film.getId(), film);

        log.info("Новый фильм добавлен {}", film);

        return film;
    }

    @PutMapping
    public Film update(@RequestBody Film film) throws ValidationException {
        log.info("PUT /films {}", film);

        Film oldFilm = films.get(film.getId());
        if (film.getId() == null || film.getId().toString().isBlank()) {
            log.debug("Не указан id {}", film.getId());
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        if (film.getName() != null && !film.getName().isBlank()) {
            log.debug("Меняем название фильма {} с {} на {}", film.getId(), oldFilm.getName(), film.getName());
            oldFilm.setName(film.getName());
            log.info("Название фильма {} изменено на {}!", oldFilm.getId(), oldFilm.getName());
        }
        if (film.getDescription() != null && !film.getDescription().isBlank()) {
            checkValidateDescription(film);
            log.debug("Меняем описание фильма {} с {} на {}", film.getId(), oldFilm.getDescription(), film.getDescription());
            oldFilm.setDescription(film.getDescription());
            log.info("Описание фильма {} изменено на {}!", oldFilm.getId(), oldFilm.getDescription());
        }
        if (film.getReleaseDate() != null) {
            checkValidateDate(film);
            log.debug("Меняем дату релиза фильма {} с {} на {}", film.getId(), oldFilm.getReleaseDate(), film.getReleaseDate());
            oldFilm.setReleaseDate(film.getReleaseDate());
            log.info("Дата релиза фильма {} изменена на {}!", oldFilm.getId(), oldFilm.getReleaseDate());
        }
        if (film.getDuration() != null) {
            checkValidateDuration(film);
            log.debug("Меняем длительность фильма {} с {} на {}", film.getId(), oldFilm.getDuration(), film.getDuration());
            oldFilm.setDuration(film.getDuration());
            log.info("Длительность фильма {} изменена на {}!", oldFilm.getId(), oldFilm.getDuration());
        }

        log.info("В информацию о фильме {} внесены изменения!", oldFilm.getId());

        return oldFilm;
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
            log.debug("Дата фильта меньше {}: {}", MIN_RELEASE_DATE, film.getReleaseDate());
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
}

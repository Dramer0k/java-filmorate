package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.controller.mapper.FilmMapper;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.request.FilmRequest;
import ru.yandex.practicum.filmorate.model.response.FilmResponse;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.Collection;
import java.util.List;


@Slf4j
@RestController
@RequestMapping("/films")
@RequiredArgsConstructor
public class FilmController  {
    public final FilmService filmService;
    public final FilmMapper mapper;

    @GetMapping
    public Collection<Film> getFilms() {
        return filmService.getAllFilms();
    }

    @PostMapping
    public FilmResponse create(@RequestBody FilmRequest request) throws ValidationException {
        Film film = mapper.toFilm(request);
        Film result = filmService.addFilm(film);
        return mapper.toResponse(result);
    }

    @PutMapping
    public FilmResponse update(@RequestBody FilmRequest request) throws ValidationException {
        Film film = mapper.toFilm(request);
        Film result = filmService.updateFilm(film);
        return mapper.toResponse(result);
    }

    @DeleteMapping
    public Film delete(@RequestParam Long filmId) throws ValidationException {
        return filmService.removeFilm(filmId);
    }

    @PutMapping("/{id}/like/{userId}")
    public void setLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.setLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{userId}")
    public void removeLike(@PathVariable Long id, @PathVariable Long userId) {
        filmService.removeFilm(id, userId);
    }

    @GetMapping("/popular")
    public List<Film> getFavoriteFilms(@RequestParam(value = "count", defaultValue = "10") String count) {
        return filmService.getFavoritesFilms(count);
    }
}

package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreStorage {

    List<Genre> findAllGenres();

    Genre getGenreById(Long id);

    List<Genre> getGenres(List<Long> genres);

    Film addGenre(Film film);
}

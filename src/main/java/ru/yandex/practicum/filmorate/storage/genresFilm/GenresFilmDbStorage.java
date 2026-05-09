package ru.yandex.practicum.filmorate.storage.genresFilm;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.GenresFilm;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Repository
public class GenresFilmDbStorage extends BaseRepository<GenresFilm> implements GenresFilmStorage {
    private final static String INSERT_QUERY = "INSERT INTO genres_film(genre_id, film_id)\n" +
            "VALUES (?, ?)";
    private final static String FIND_ALL_GENRE_QUERY = "SELECT * FROM genres_film WHERE film_id = ?\n" +
            "ORDER BY genre_id";

    public GenresFilmDbStorage(JdbcTemplate jdbc, RowMapper<GenresFilm> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public void addGenresFilm(Film film) throws InternalServerException {
        if (film.getGenres() != null) {
            for (Genre genre : film.getGenres()) {
                insertWithoutId(INSERT_QUERY, genre.getId(), film.getId());
            }
        }
    }

    @Override
    public List<Long> getGenres(Long filmId) {
        List<GenresFilm> list = findMany(FIND_ALL_GENRE_QUERY, filmId);
        List<Long> result = new ArrayList<>();
        for (GenresFilm genresFilm : list) {
            result.add(genresFilm.getGenreId());
        }
        return result;
    }
}

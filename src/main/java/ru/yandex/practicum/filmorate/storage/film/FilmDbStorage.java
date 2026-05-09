package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.util.Collection;
import java.util.List;

@Slf4j
@Primary
@Repository
public class FilmDbStorage extends BaseRepository<Film> implements FilmStorage{
    private final static String FIND_ALL_QUERY = "SELECT * FROM films";
    private static final String INSERT_QUERY = "INSERT INTO films(name, description, release_date, duration, rating_id)\n" +
            "VALUES (?, ?, ?, ?, ?)";
    private static final String FIND_FILM_BY_ID_QUERY = "SELECT * FROM films WHERE id = ?";
    private static final String DELETE_BY_ID_QUERY = "DELETE FROM films WHERE id = ?";
    private static final String UPDATE_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, rating_id = ? ";
    private static final String INSERT_LIKE_QUERY = "INSERT INTO favorites_film(film_id, user_id)\n" +
            "VALUES (?, ?)";
    private static final String DELETE_LIKE_QUERY = "DELETE FROM favorites_film WHERE user_id = ? and film_id = ?";
    private static final String FIND_FAVORITES_FILMS_QUERY = "SELECT * FROM films as f\n" +
            "RIGHT JOIN (SELECT film_id, COUNT(user_id) FROM favorites_film\n" +
            "GROUP BY film_id\n" +
            "ORDER BY COUNT(user_id) DESC) as ff ON f.id = ff.film_id\n" +
            "LIMIT ?";

    public FilmDbStorage(JdbcTemplate jdbc, RowMapper<Film> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public Collection<Film> getAllFilms() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Film addFilm(Film film) throws ValidationException, InternalServerException {
        long id = insert(
                INSERT_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );
        film.setId(id);
        film.setMpaId(film.getMpa().getId());
        return film;
    }

    @Override
    public Film removeFilm(Long filmId) {
        Film film = null;
        if(findOne(FIND_FILM_BY_ID_QUERY, filmId).isPresent()) {
            film = findOne(FIND_FILM_BY_ID_QUERY, filmId).get();
        } else {
            throw new NotFoundException("Фильм не найден!");
        }

        boolean result = delete(DELETE_BY_ID_QUERY, filmId);
        if (result) {
            return film;
        } else {
            throw new ConditionsNotMetException("Не удалось удалить фильм");
        }
    }

    @Override
    public Film updateFilm(Film film) throws ValidationException, InternalServerException {
        Film oldFilm = getFilm(film.getId());

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

        if (film.getGenres().isEmpty()) {
            film.setGenres(oldFilm.getGenres());
        }
        film.setMpaId(film.getMpa().getId());

        update(
                UPDATE_QUERY,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );
        return film;
    }

    @Override
    public void setLike(Long id, Long userId) throws InternalServerException {
        insertWithoutId(INSERT_LIKE_QUERY, id, userId);
    }

    @Override
    public void removeLike(Long id, Long userId) {
        delete(DELETE_LIKE_QUERY, userId, id);
    }

    @Override
    public List<Film> getFavoritesFilms(String count) {
        return findMany(FIND_FAVORITES_FILMS_QUERY, count);
    }

    @Override
    public Film getFilm(Long filmId) {
        Film film;
        if (findOne(FIND_FILM_BY_ID_QUERY, filmId).isPresent()) {
            film = findOne(FIND_FILM_BY_ID_QUERY, filmId).get();
        } else {
            throw new NotFoundException("Фильм не найден!");
        }
        return film;
    }
}

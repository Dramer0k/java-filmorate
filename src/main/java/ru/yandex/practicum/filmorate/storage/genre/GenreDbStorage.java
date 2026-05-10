package ru.yandex.practicum.filmorate.storage.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class GenreDbStorage extends BaseRepository<Genre> implements GenreStorage {
    private static final  String FIND_ALL_QUERY = "SELECT * FROM genre";
    private static final  String FIND_GENRE_BY_ID_QUERY = "SELECT * FROM genre WHERE id = ?";

    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Genre> findAllGenres() {
        return findMany(FIND_ALL_QUERY);
    }

    @Override
    public Film addGenre(Film film) {
        if (film.getGenres() == null || film.getGenres().isEmpty()) {
            return film;
        }

        List<Long> genreIds = film.getGenres().stream()
                .map(Genre::getId)
                .collect(Collectors.toList());

        Set<Genre> genres = new HashSet<>(findGenresByIds("genre", genreIds));
        List<Genre> sortedGenres = genres.stream()
                .sorted(Comparator.comparingLong(Genre::getId))
                .toList();

        film.setGenres(sortedGenres);
        return film;
    }

    @Override
    public Genre getGenreById(Long id) {
        Genre genre;
        if (findOne(FIND_GENRE_BY_ID_QUERY, id).isPresent()) {
            genre = findOne(FIND_GENRE_BY_ID_QUERY, id).get();
            return genre;
        } else {
            throw new NotFoundException("Жанр с таким id не найден!");
        }
    }

    @Override
    public List<Genre> getGenres(List<Long> genres) {
        return findGenresByIds("genre", genres);
    }
}

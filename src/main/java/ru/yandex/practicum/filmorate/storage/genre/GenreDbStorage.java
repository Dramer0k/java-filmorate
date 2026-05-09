package ru.yandex.practicum.filmorate.storage.genre;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.util.*;

@Slf4j
@Repository
public class GenreDbStorage extends BaseRepository<Genre> implements GenreStorage {
    private final static String FIND_ALL_QUERY = "SELECT * FROM genre";
    private final static String FIND_GENRE_BY_ID_QUERY = "SELECT * FROM genre WHERE id = ?";

    public GenreDbStorage(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper);
    }

    @Override
    public List<Genre> findAllGenres() {
        return findMany(FIND_ALL_QUERY);
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
        List<Genre> list = new ArrayList<>();
        for (Long id : genres) {
            list.add(getGenreById(id));
        }
        return list;
    }

    @Override
    public Film addGenre(Film film) {
        Set<Genre> genres = new HashSet<>();
        for (Genre genre : film.getGenres()) {
            Genre result = getGenreById(genre.getId());
            genres.add(result);
        }
        List<Genre> sortedGenres = genres.stream()
                .sorted(Comparator.comparingLong(Genre::getId)) // сортировка по id в порядке возрастания
                .toList();

        film.setGenres(sortedGenres);
        return film;
    }
}

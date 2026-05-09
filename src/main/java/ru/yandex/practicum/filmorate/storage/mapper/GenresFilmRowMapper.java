package ru.yandex.practicum.filmorate.storage.mapper;

import jakarta.annotation.Nonnull;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Controller;
import ru.yandex.practicum.filmorate.model.GenresFilm;

import java.sql.ResultSet;
import java.sql.SQLException;

@Controller
public class GenresFilmRowMapper implements RowMapper<GenresFilm> {

    @Override
    public GenresFilm mapRow(@Nonnull ResultSet resultSet, int rowNum) throws SQLException {
        GenresFilm genresFilm = new GenresFilm();
        genresFilm.setGenre_id(resultSet.getLong("genre_id"));
        genresFilm.setFilm_id(resultSet.getLong("film_id"));

        return genresFilm;
    }
}

package ru.yandex.practicum.filmorate.storage.mapper;

import jakarta.annotation.Nonnull;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Controller;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;

@Controller
public class GenreRowMapper implements RowMapper<Genre> {

    @Override
    public Genre mapRow(@Nonnull ResultSet resultSet, int rowNum) throws SQLException {
        Genre genre = new Genre();
        genre.setId(resultSet.getLong("id"));
        genre.setName(resultSet.getString("name"));

        return genre;
    }
}

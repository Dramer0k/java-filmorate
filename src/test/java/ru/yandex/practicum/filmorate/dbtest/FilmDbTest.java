package ru.yandex.practicum.filmorate.dbtest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.jdbc.core.JdbcTemplate;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.film.FilmDbStorage;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class FilmDbTest {
    private final FilmDbStorage filmStorage;
    private final JdbcTemplate jdbcTemplate;
    Film testFilm = new Film();
    Mpa mpa = new Mpa();


    @Test
    void testDatabaseConnection() {
        String sql = "SELECT COUNT(*) FROM films";
        Long count = jdbcTemplate.queryForObject(sql, Long.class);
        assertNotNull(count);
        assertTrue(count >= 0);
    }

    @Test
    void testGetAllFilms() {
        Collection<Film> filmsMap = filmStorage.getAllFilms();

        assertThat(filmsMap).isNotNull()
                .anyMatch(film -> film.getName().equals("Holly"));
    }

    @Test
    void testAddFilm() throws ValidationException, InternalServerException {
        mpa.setId(1L);

        testFilm.setName("Vesna");
        testFilm.setDescription("samui krutoi filmec");
        testFilm.setReleaseDate(LocalDate.of(2002, 10, 1));
        testFilm.setDuration(110);
        testFilm.setMpa(mpa);

        Film film = filmStorage.addFilm(testFilm);

        assertThat(film).isNotNull()
                .hasFieldOrPropertyWithValue("name", "Vesna")
                .hasFieldOrPropertyWithValue("description", "samui krutoi filmec")
                .hasFieldOrPropertyWithValue("duration", 110)
                .hasFieldOrPropertyWithValue("MpaId", 1L)
                .extracting("releaseDate")
                .isEqualTo(LocalDate.of(2002, 10, 1));
    }

    @Test
    void testRemoveFilm() {
        filmStorage.removeFilm(1L);
        Collection<Film> filmMap = filmStorage.getAllFilms();

        assertThat(filmMap).noneMatch(film -> film.getId().equals(1L));
    }

    @Test
    void testUpdateFilm() throws ValidationException, InternalServerException {
        mpa.setId(1L);

        testFilm.setId(1L);
        testFilm.setName("Loly");
        testFilm.setDescription("super film");
        testFilm.setReleaseDate(LocalDate.of(2000, 10, 10));
        testFilm.setDuration(100);
        testFilm.setMpa(mpa);

        filmStorage.updateFilm(testFilm);

        Collection<Film> filmMap = filmStorage.getAllFilms();

        assertThat(filmMap).isNotNull()
                .anyMatch(film -> film.getName().equals("Loly"));
    }

    @Test
    void testSetLike() throws InternalServerException {
        filmStorage.setLike(1L, 1L);

        Integer likeCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM favorites_film WHERE film_id = ? AND user_id = ?",
                Integer.class,
                "1",
                "1"
        );

        assertThat(likeCount).isEqualTo(1);
    }

    @Test
    void testRemoveLike() throws InternalServerException {
        filmStorage.setLike(1L, 1L);
        filmStorage.removeLike(1L, 1L);

        Integer likeCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM favorites_film WHERE film_id = ? AND user_id = ?",
                Integer.class,
                "1",
                "1"
        );

        assertThat(likeCount).isEqualTo(0);
    }

    @Test
    void testGetFavoritesFilms() throws InternalServerException {
        filmStorage.setLike(1L, 1L);
        filmStorage.setLike(1L, 2L);
        filmStorage.setLike(2L, 1L);

        List<Film> favoritesMap = filmStorage.getFavoritesFilms("1");

        assertThat(favoritesMap).isNotNull()
                .anyMatch(film -> film.getName().equals("Holly"));
        assertThat(favoritesMap).noneMatch(film -> film.getName().equals("Vilar"));
    }

    @Test
    void testGetFilm() {
        Film film = filmStorage.getFilm(1L);

        assertThat(film).hasFieldOrPropertyWithValue("name", "Holly");

    }







}

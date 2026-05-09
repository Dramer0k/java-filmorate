package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.BaseRepository;

import java.util.List;

@Slf4j
@Repository
public class MpaDbStorage extends BaseRepository<Mpa> implements MpaStorage {
    private static final String FIND_ALL_MPA_QUERY = "SELECT * FROM rating";
    private static final String FIND_MPA_DY_ID_QUERY = "SELECT * FROM rating WHERE id = ?";

    public MpaDbStorage(JdbcTemplate jdbc, RowMapper<Mpa> mapper) {
        super(jdbc, mapper);
    }

    public List<Mpa> findAllMpa() {
        return findMany(FIND_ALL_MPA_QUERY);
    }

    @Override
    public Mpa findMpaById(Long id) {
        if (findOne(FIND_MPA_DY_ID_QUERY, id).isPresent()) {
            return findOne(FIND_MPA_DY_ID_QUERY, id).get();
        } else {
            throw new NotFoundException("Такого MPA не существует");
        }
    }

    @Override
    public Film addMpa(Film film) {
        Mpa mpa = new Mpa();
        mpa = findMpaById(film.getMpa().getId());
        film.setMpa(mpa);
        return film;
    }
}

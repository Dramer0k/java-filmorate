package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.storage.mpa.MpaStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MpaService {
    private final MpaStorage mpaStorage;

    public List<Mpa> findAllMpa() {
        log.info("GET /mpa");
        return mpaStorage.findAllMpa();
    }

    public Mpa findMpaById(Long id) {
        log.info("GET /mpa/{}", id);
        Mpa result = mpaStorage.findMpaById(id);
        log.info("Полученный рейтинг: {}", result);
        return result;
    }

    public Film addMpa(Film film) {
        log.info("Добавить рейтинг");
        Film result = mpaStorage.addMpa(film);
        log.info("Рейтинг добавлен: {}", result.getMpa());
        return result;
    }
}

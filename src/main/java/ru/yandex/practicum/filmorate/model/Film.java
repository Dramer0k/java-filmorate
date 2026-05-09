package ru.yandex.practicum.filmorate.model;


import lombok.Data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Film.
 */
@Data
public class Film {
    private Long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
    private int likeCount = 0;
    private List<Genre> genres = new ArrayList<>();
    private Mpa mpa;
    private Long MpaId;
}

package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
public class User {
    private Long id;
    private String name;
    private String login;
    private String email;
    private LocalDate birthday;
    private Map<Long, Boolean> friends = new HashMap<>();
    private Set<Long> favoriteFilms = new HashSet<>();
}

package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class Mpa implements Serializable {
    private Long id;
    private String name;
}

package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final Map<Long, User> users = new HashMap<>();

    @GetMapping
    public Collection<User> getFilm() {
        log.info("GET /users");
        return users.values();
    }

    @PostMapping
    public User create(@RequestBody User user) throws ValidationException {
        log.info("POST /users {}", user);

        checkValidateLogin(user);
        checkValidateName(user);
        checkValidateEmail(user);
        checkValidateBirthday(user);

        user.setId(getNextId());

        users.put(user.getId(), user);
        log.info("Новый пользователь {} добавлен", user);
        return user;
    }

    @PutMapping
    public User update(@RequestBody User user) throws ValidationException {
        log.info("PUT /users {}", user);

        User oldUser = users.get(user.getId());
        if (user.getId() == null || user.getId().toString().isBlank()) {
            log.debug("не указан id {}", user.getId());
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        if (user.getName() != null && !user.getName().isBlank()) {
            log.debug("Пользователь с id: {}. Меняем имя {} на {}", user.getId(), oldUser.getName(), user.getName());
            oldUser.setName(user.getName());
            log.info("У пользователя с id {} изменено имя на {}", user.getId(), user.getName());
        }
        if (user.getLogin() != null) {
            checkValidateLogin(user);
            oldUser.setLogin(user.getLogin());
        }
        if (user.getEmail() != null) {
            checkValidateEmail(user);
            log.debug("Пользователь с id: {}. Меняем почту {} на {}", user.getId(), oldUser.getEmail(), user.getEmail());
            oldUser.setEmail(user.getEmail());
            log.info("У пользователя с id {} изменена почта на {}", user.getId(), user.getEmail());
        }
        if (oldUser.getBirthday() != null) {
            checkValidateBirthday(user);
            log.debug("Пользователь с id: {}. Меняем дату рождения {} на {}", user.getId(), oldUser.getBirthday(), user.getBirthday());
            oldUser.setBirthday(user.getBirthday());
            log.info("У пользователя с id {} изменена дата рождения на {}", user.getId(), user.getBirthday());
        }
        log.info("Данные пользователя изменены!");

        return oldUser;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }

    public void checkValidateEmail(User user) throws ValidationException {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            log.debug("Неверный формат почты {}", user.getEmail());
            throw new ValidationException("Неверный формат почты");
        }
    }

    public void checkValidateLogin(User user) throws ValidationException {
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().trim().contains(" ")) {
            log.debug("Неверный формат логина {}", user.getLogin());
            throw new ValidationException("Неверный формат логина");
        }
    }

    public void checkValidateName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Имя пользователя не было введено, указываем логин {}", user.getLogin());
            user.setName(user.getLogin());
            log.info("Присвоено имя {}", user.getName());
        }
    }

    public void checkValidateBirthday(User user) throws ValidationException {
        if (user.getBirthday() == null) {
            log.debug("Дата рождения не указана");
            throw new ValidationException("Дата рождения не может быть пустой");
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.debug("Неверная дата рождения {}", user.getBirthday());
            throw new ValidationException("Неверно указана дата рождения");
        }

    }
}
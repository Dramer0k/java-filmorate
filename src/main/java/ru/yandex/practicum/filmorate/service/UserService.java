package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    public final UserStorage userStorage;

    public Collection<User> getUsersMap() {
        log.info("GET /users");
        return userStorage.getUsersMap();
    }

    public User getUserById(Long id) {
        log.info("GET /{}", id);
        User result = userStorage.getUserById(id);
        log.info("Получен пользователь: {}", result);
        return result;
    }

    public User createUser(User user) throws ValidationException, InternalServerException {
        log.info("POST /users {}", user);

        checkValidateLogin(user);
        checkValidateName(user);
        checkValidateEmail(user);
        checkValidateBirthday(user);

        log.info("Валидация прошла успешно. Создаем пользователя...");

        User result = userStorage.createUser(user);
        log.info("Пользователь создан: {}", result);

        return result;
    }

    public User updateUserInfo(User user) throws ValidationException, InternalServerException {
        log.info("PUT /users {}", user);

        if (user.getId() == null || user.getId().toString().isBlank()) {
            log.debug("не указан id {}", user.getId());
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        User oldUser = userStorage.getUserById(user.getId());
        if (oldUser == null) {
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден!");
        }

        checkValidateLogin(user);
        checkValidateBirthday(user);
        checkValidateEmail(user);

        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Пользователь с id: {}. Меняем имя {} на {}", user.getId(), oldUser.getName(), user.getName());
            user.setName(oldUser.getName());
            log.info("У пользователя с id {} изменено имя на {}", user.getId(), user.getName());
        }
        if (user.getLogin() == null) {
            user.setLogin(oldUser.getLogin());
        }
        if (user.getEmail() == null) {
            log.debug("Пользователь с id: {}. Меняем почту на {}", user.getId(), oldUser.getEmail());
            user.setEmail(oldUser.getEmail());
            log.info("У пользователя с id {} изменена почта на {}", user.getId(), user.getEmail());
        }
        if (user.getBirthday() == null) {
            log.debug("Пользователь с id: {}. Меняем дату рождения на {}", user.getId(), oldUser.getBirthday());
            user.setBirthday(oldUser.getBirthday());
            log.info("У пользователя с id {} изменена дата рождения на {}", user.getId(), user.getBirthday());
        }

        return userStorage.updateUserInfo(user);
    }

    public User removeUser(Long userId) {
        log.info("DELETE /users?userId={}", userId);
        if (userId == null) {
            log.debug("Не указан id: {}", userId);
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        log.info("Удалить пользователя с id {}", userId);

        return userStorage.removeUser(userId);
    }

    public void addFriend(Long id, Long friendId) throws InternalServerException {
        log.info("PUT /users/{}/friends/{}", id, friendId);
        log.info("User: {} добавляет в друзья User: {}", id, friendId);
        userStorage.addFriend(id, friendId);
    }

    public void removeFriend(Long id, Long friendId) {
        log.info("DELETE /users/{}/friends/{}", id, friendId);
        userStorage.removeFriend(id, friendId);
    }

    public List<User> getAllFriends(Long id) {
        log.info("GET /users/{}/friends", id);
        log.info("Получить список друзей пользователя: {}", id);
        return userStorage.getAllFriends(id);
    }

    public List<User> getMutualFriends(Long id, Long friendId) {
        log.info("GET /users/{}/friends/common/{}", id, friendId);
        log.info("Получить список общих друзей пользователя: {}", id);
        return userStorage.getMutualFriends(id, friendId);
    }

    private void checkValidateEmail(User user) throws ValidationException {
        if (user.getEmail() == null || !user.getEmail().contains("@")) {
            log.debug("Неверный формат почты {}", user.getEmail());
            throw new ValidationException("Неверный формат почты");
        }
    }

    private void checkValidateLogin(User user) throws ValidationException {
        if (user.getLogin() == null || user.getLogin().isBlank() || user.getLogin().trim().contains(" ")) {
            log.debug("Неверный формат логина {}", user.getLogin());
            throw new ValidationException("Неверный формат логина");
        }
    }

    private void checkValidateName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.debug("Имя пользователя не было введено, указываем логин {}", user.getLogin());
            user.setName(user.getLogin());
            log.info("Присвоено имя {}", user.getName());
        }
    }

    private void checkValidateBirthday(User user) throws ValidationException {
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

package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.filmorate.storage.user.UserStorage;
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

    public User createUser(User user) throws ValidationException {
        log.info("POST /users {}", user);

        checkValidateLogin(user);
        checkValidateName(user);
        checkValidateEmail(user);
        checkValidateBirthday(user);

        return userStorage.createUser(user);
    }

    public User updateUserInfo(User user) throws ValidationException {
        log.info("PUT /users {}", user);

        checkValidateLogin(user);
        checkValidateBirthday(user);
        checkValidateEmail(user);

        if (user.getId() == null || user.getId().toString().isBlank()) {
            log.debug("не указан id {}", user.getId());
            throw new ConditionsNotMetException("Id должен быть указан");
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

    public void addFriend(String id, String friendId) {
        log.info("Добавляем в друзья");
        userStorage.addFriend(id, friendId);
    }

    public void removeFriend(String id, String friendId) {
        log.info("Удалить друга");
        userStorage.removeFriend(id, friendId);
    }

    public List<User> getAllFriends(String id) {
        log.info("Получить список друзей");
        return userStorage.getAllFriends(id);
    }

    public List<User> getMutualFriends(String id, String friendId) {
        log.info("Получить список общих друзей");
        return userStorage.getMutualFriends(id, friendId);
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

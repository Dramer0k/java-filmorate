package ru.yandex.filmorate.storage.user;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Data
@Component
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long id = 1;

    @Override
    public Collection<User> getUsersMap() {
        return users.values();
    }

    @Override
    public User createUser(User user) {
        user.setId(id);
        id++;
        users.put(user.getId(), user);
        log.info("Новый пользователь {} добавлен", user);
        return user;
    }

    @Override
    public User updateUserInfo(User user) {
        User oldUser = users.get(user.getId());
        if (oldUser == null) {
            throw new NotFoundException("Пользователь с id " + user.getId() + " не найден!");
        }

        if (user.getName() != null && !user.getName().isBlank()) {
            log.debug("Пользователь с id: {}. Меняем имя {} на {}", user.getId(), oldUser.getName(), user.getName());
            oldUser.setName(user.getName());
            log.info("У пользователя с id {} изменено имя на {}", user.getId(), user.getName());
        }
        if (user.getLogin() != null) {
            oldUser.setLogin(user.getLogin());
        }
        if (user.getEmail() != null) {
            log.debug("Пользователь с id: {}. Меняем почту {} на {}", user.getId(), oldUser.getEmail(), user.getEmail());
            oldUser.setEmail(user.getEmail());
            log.info("У пользователя с id {} изменена почта на {}", user.getId(), user.getEmail());
        }
        if (oldUser.getBirthday() != null) {
            log.debug("Пользователь с id: {}. Меняем дату рождения {} на {}", user.getId(), oldUser.getBirthday(), user.getBirthday());
            oldUser.setBirthday(user.getBirthday());
            log.info("У пользователя с id {} изменена дата рождения на {}", user.getId(), user.getBirthday());
        }
        log.info("Данные пользователя изменены!");
        return user;
    }

    @Override
    public User removeUser(Long userId) {
        User oldUser = users.get(userId);
        users.remove(userId);

        log.info("Пользователь {} с id {} удален", oldUser.getName(), userId);

        return oldUser;
    }

    @Override
    public void addFriend(String id, String friendId) {
        User user = users.get(Long.valueOf(id));
        User friend = users.get(Long.valueOf(friendId));
        if  (user == null || friend == null) {
            throw new NotFoundException("Пользователь не найден!");
        }
        if (user.getFriends().contains(friend.getId())) {
            throw new ConditionsNotMetException("Такой друг уже есть!");
        }
        user.getFriends().add(friend.getId());
        friend.getFriends().add(user.getId());
        log.info("Друг с id {} добавлен", friend.getId());
    }

    @Override
    public void removeFriend(String id, String friendId) {
        User user = users.get(Long.valueOf(id));
        User friend = users.get(Long.valueOf(friendId));
        if  (user == null || friend == null) {
            throw new NotFoundException("Пользователь не найден!");
        }
        if (!user.getFriends().contains(friend.getId())) {
            throw new ConditionsNotMetException("Такого друга нет в вашем списке!");
        }
        user.getFriends().remove(friend.getId());
        friend.getFriends().remove(user.getId());

        log.info("Друг с id {} удален", friend.getId());
    }

    @Override
    public List<User> getAllFriends(String id) {
        User user = users.get(Long.valueOf(id));
        if (user == null) {
            throw new NotFoundException("Пользователя с id " + id + " не существует!");
        }
        return user.getFriends().stream()
                .map(users::get)
                .toList();
    }

    @Override
    public User getById(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователя с id " + id + " нет");
        }
        log.info("Пользователь: {}", users.get(id));
        return users.get(id);
    }

    @Override
    public List<User> getMutualFriends(String id, String friendId) {
        User user = users.get(Long.valueOf(id));
        User friend = users.get(Long.valueOf(friendId));
        return user.getFriends().stream()
                .filter(friend.getFriends()::contains)
                .map(users::get)
                .toList();
    }
}
